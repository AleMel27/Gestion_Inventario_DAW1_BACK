package com.gestionInventario.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.dtos.request.CompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO;
import com.gestionInventario.dtos.response.CompraResumenDTO;
import com.gestionInventario.exception.ResourceNotFoundException;
import com.gestionInventario.mapper.CompraMapper;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.model.Compra;
import com.gestionInventario.model.DetalleCompra;
import com.gestionInventario.model.Producto;
import com.gestionInventario.model.Proveedor;
import com.gestionInventario.model.TipoComprobante;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IAlmacenRepository;
import com.gestionInventario.repository.ICompraRepository;
import com.gestionInventario.repository.IDetalleCompraRepository;
import com.gestionInventario.repository.IProductoRepository;
import com.gestionInventario.repository.IProveedorRepository;
import com.gestionInventario.repository.ITipoComprobanteRepository;
import com.gestionInventario.repository.IUsuarioRepository;

@Service
public class CompraService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_RECIBIDA = "RECIBIDA";
    private static final String ESTADO_ANULADA = "ANULADA";
    private static final String CODIGO_MOVIMIENTO_ENTRADA = "ENTRADA";

    @Autowired
    private ICompraRepository compraRepo;

    @Autowired
    private IDetalleCompraRepository detalleRepo;

    @Autowired
    private IProveedorRepository proveedorRepo;

    @Autowired
    private IUsuarioRepository usuarioRepo;

    @Autowired
    private IAlmacenRepository almacenRepo;

    @Autowired
    private ITipoComprobanteRepository tipoComprobanteRepo;

    @Autowired
    private IProductoRepository productoRepo;

    @Autowired
    private CompraMapper compraMapper;

    @Autowired
    private MovimientoInventarioService movimientoInventarioService;

    @Transactional(readOnly = true)
    public Page<CompraResumenDTO> listarPaginado(Pageable pageable) {
        return compraRepo.findAll(pageable).map(compraMapper::convertirAResumenDto);
    }

    @Transactional(readOnly = true)
    public CompraResDTO obtenerPorId(Long id) {
        Compra compra = obtenerCompraExistente(id);
        return convertirCompraConDetalles(compra);
    }

    @Transactional
    public CompraResDTO registrarCompra(CompraDTO dto) {
        Proveedor proveedor = proveedorRepo.findById(dto.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con ID: " + dto.getIdProveedor()));

        Usuario usuario = usuarioRepo.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + dto.getIdUsuario()));

        Almacen almacen = almacenRepo.findById(dto.getIdAlmacen())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Almacén no encontrado con ID: " + dto.getIdAlmacen()));

        TipoComprobante tipoComprobante = tipoComprobanteRepo.findById(dto.getIdTipoComprobante())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de comprobante no encontrado con ID: " + dto.getIdTipoComprobante()));

        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setUsuario(usuario);
        compra.setAlmacen(almacen);
        compra.setTipoComprobante(tipoComprobante);
        compra.setNumeroComprobante(dto.getNumeroComprobante());
        compra.setObservacion(dto.getObservacion());
        compra.setEstado(ESTADO_PENDIENTE);

        List<DetalleCompra> detalles = dto.getDetalles().stream()
                .map(detalleDto -> {
                    Producto producto = productoRepo.findById(detalleDto.getIdProducto())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Producto no encontrado con ID: " + detalleDto.getIdProducto()));

                    DetalleCompra detalle = new DetalleCompra();
                    detalle.setCompra(compra);
                    detalle.setProducto(producto);
                    detalle.setCantidad(detalleDto.getCantidad());
                    detalle.setCostoUnitario(detalleDto.getCostoUnitario());
                    return detalle;
                })
                .collect(Collectors.toList());

        compra.setTotal(calcularTotal(detalles));
        Compra compraGuardada = compraRepo.save(compra);

        detalles.forEach(detalle -> {
            detalle.setCompra(compraGuardada);
            detalleRepo.save(detalle);
        });

        return convertirCompraConDetalles(compraGuardada);
    }

    @Transactional
    public CompraResDTO recibirCompra(Long id) {
        Compra compra = obtenerCompraExistente(id);

        if (!ESTADO_PENDIENTE.equals(compra.getEstado())) {
            throw new IllegalArgumentException("Solo se puede recibir una compra en estado PENDIENTE");
        }

        List<DetalleCompra> detalles = detalleRepo.findByCompra_IdCompra(compra.getIdCompra());
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("No se puede recibir una compra sin detalles");
        }

        for (DetalleCompra detalle : detalles) {
            movimientoInventarioService.registrarMovimientoPorCodigo(
                    detalle.getProducto().getIdProducto(),
                    compra.getAlmacen().getIdAlmacen(),
                    compra.getUsuario().getIdUsuario(),
                    CODIGO_MOVIMIENTO_ENTRADA,
                    detalle.getCantidad(),
                    "INGRESO POR COMPRA - COMPROBANTE N°: " + compra.getNumeroComprobante(),
                    compra.getTipoComprobante().getCodigo(),
                    compra.getIdCompra());
        }

        compra.setEstado(ESTADO_RECIBIDA);
        Compra actualizada = compraRepo.save(compra);
        return convertirCompraConDetalles(actualizada);
    }

    @Transactional
    public CompraResDTO anularCompra(Long id) {
        Compra compra = obtenerCompraExistente(id);

        if (ESTADO_RECIBIDA.equals(compra.getEstado())) {
            throw new IllegalArgumentException("No se puede anular una compra RECIBIDA sin reversar inventario");
        }

        if (ESTADO_ANULADA.equals(compra.getEstado())) {
            throw new IllegalArgumentException("La compra ya se encuentra ANULADA");
        }

        if (!ESTADO_PENDIENTE.equals(compra.getEstado())) {
            throw new IllegalArgumentException("Solo se puede anular una compra en estado PENDIENTE");
        }

        compra.setEstado(ESTADO_ANULADA);
        Compra actualizada = compraRepo.save(compra);
        return convertirCompraConDetalles(actualizada);
    }

    private Compra obtenerCompraExistente(Long id) {
        return compraRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + id));
    }

    private CompraResDTO convertirCompraConDetalles(Compra compra) {
        List<DetalleCompra> detalles = detalleRepo.findByCompra_IdCompra(compra.getIdCompra());
        return compraMapper.convertirADto(compra, detalles);
    }

    private BigDecimal calcularTotal(List<DetalleCompra> detalles) {
        return detalles.stream()
                .map(this::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSubtotal(DetalleCompra detalle) {
        return detalle.getCantidad()
                .multiply(detalle.getCostoUnitario())
                .setScale(2, RoundingMode.HALF_UP);
    }
}

package com.gestionInventario.services;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.dtos.request.MovimientoInventarioCreateDTO;
import com.gestionInventario.dtos.response.MovimientoInventarioResDTO;
import com.gestionInventario.exception.BusinessRuleException;
import com.gestionInventario.exception.ResourceNotFoundException;
import com.gestionInventario.mapper.MovimientoInventarioMapper;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.model.Compra;
import com.gestionInventario.model.Inventario;
import com.gestionInventario.model.MovimientoInventario;
import com.gestionInventario.model.Producto;
import com.gestionInventario.model.TipoMovimiento;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IAlmacenRepository;
import com.gestionInventario.repository.ICompraRepository;
import com.gestionInventario.repository.IInventarioRepository;
import com.gestionInventario.repository.IMovimientoInventarioRepository;
import com.gestionInventario.repository.IProductoRepository;
import com.gestionInventario.repository.ITipoMovimientoRepository;
import com.gestionInventario.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final IMovimientoInventarioRepository movimientoRepo;
    private final IInventarioRepository inventarioRepo;
    private final IProductoRepository productoRepo;
    private final IAlmacenRepository almacenRepo;
    private final IUsuarioRepository usuarioRepo;
    private final ITipoMovimientoRepository tipoMovimientoRepo;
    private final ICompraRepository compraRepo;
    private final MovimientoInventarioMapper movimientoMapper;

    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResDTO> listarPaginado(Pageable pageable) {
        return movimientoRepo.findAll(pageable).map(movimientoMapper::convertirADto);
    }

    @Transactional(readOnly = true)
    public MovimientoInventarioResDTO obtenerPorId(Long id) {
        return movimientoMapper.convertirADto(obtenerMovimientoExistente(id));
    }

    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResDTO> listarHistorialProductoAlmacen(
            Long idProducto,
            Long idAlmacen,
            Pageable pageable) {

        return movimientoRepo.findByProducto_IdProductoAndAlmacen_IdAlmacen(idProducto, idAlmacen, pageable)
                .map(movimientoMapper::convertirADto);
    }

    @Transactional
    public MovimientoInventarioResDTO registrarManual(MovimientoInventarioCreateDTO dto) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        TipoMovimiento tipoMovimiento = obtenerTipoMovimientoActivo(dto.getIdTipoMovimiento());
        validarAutorizacionPorTipoMovimiento(tipoMovimiento);

        MovimientoInventario movimiento = registrarMovimientoInterno(
                dto.getIdProducto(),
                dto.getIdAlmacen(),
                usuarioAutenticado,
                tipoMovimiento,
                dto.getCantidad(),
                dto.getMotivo(),
                dto.getReferencia(),
                null);

        return movimientoMapper.convertirADto(movimiento);
    }

    @Transactional
    public MovimientoInventario registrarMovimiento(
            Long idProducto,
            Long idAlmacen,
            Long idUsuario,
            Integer idTipoMovimiento,
            BigDecimal cantidad,
            String motivo,
            String referencia,
            Long idCompra) {

        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        TipoMovimiento tipoMovimiento = obtenerTipoMovimientoActivo(idTipoMovimiento);

        Compra compra = null;
        if (idCompra != null) {
            compra = compraRepo.findById(idCompra)
                    .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + idCompra));
        }

        return registrarMovimientoInterno(
                idProducto,
                idAlmacen,
                usuario,
                tipoMovimiento,
                cantidad,
                motivo,
                referencia,
                compra);
    }

    private MovimientoInventario registrarMovimientoInterno(
            Long idProducto,
            Long idAlmacen,
            Usuario usuario,
            TipoMovimiento tipoMovimiento,
            BigDecimal cantidad,
            String motivo,
            String referencia,
            Compra compra) {

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a cero");
        }

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));

        Almacen almacen = almacenRepo.findById(idAlmacen)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + idAlmacen));

        // 3. Calcular el nuevo stock según el signoStock (+1, -1, 0)
        short signo = tipoMovimiento.getSignoStock();
        Inventario inventario = obtenerInventarioParaModificar(idProducto, idAlmacen, signo);

        BigDecimal stockAnterior = inventario.getStockActual();
        BigDecimal stockPosterior;

        if (signo > 0) {
            // Entrada / Incremento
            stockPosterior = stockAnterior.add(cantidad);
        } else if (signo < 0) {
            // Salida / Decremento (Validación de Stock)
            if (stockAnterior.compareTo(cantidad) < 0) {
                throw new BusinessRuleException("Stock insuficiente en el almacén. Stock actual: " 
                        + stockAnterior + ", Cantidad requerida: " + cantidad);
            }
            stockPosterior = stockAnterior.subtract(cantidad);
        } else {
            // Ajuste directo (signoStock = 0)
            stockPosterior = cantidad;
        }

        // 4. Actualizar inventario
        inventario.setStockActual(stockPosterior);
        inventarioRepo.save(inventario);

        // 5. Registrar la trazabilidad en Kardex (MovimientoInventario)
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setCompra(compra);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockPosterior(stockPosterior);
        movimiento.setMotivo(motivo);
        movimiento.setReferencia(referencia);

        return movimientoRepo.save(movimiento);
    }

    private Inventario obtenerInventarioParaModificar(Long idProducto, Long idAlmacen, short signoStock) {
        if (signoStock < 0) {
            return inventarioRepo.findByProductoAndAlmacenForUpdate(idProducto, idAlmacen)
                    .orElseThrow(() -> new BusinessRuleException(
                            "No existe stock para el producto " + idProducto + " en el almacén " + idAlmacen));
        }

        inventarioRepo.insertarSiNoExiste(idProducto, idAlmacen);

        return inventarioRepo.findByProductoAndAlmacenForUpdate(idProducto, idAlmacen)
                .orElseThrow(() -> new IllegalStateException(
                        "No se pudo obtener el inventario para el producto "
                                + idProducto + " en el almacén " + idAlmacen));
    }

    @Transactional
    public MovimientoInventario registrarMovimientoPorCodigo(
            Long idProducto,
            Long idAlmacen,
            Long idUsuario,
            String codigoTipoMovimiento,
            BigDecimal cantidad,
            String motivo,
            String referencia,
            Long idCompra) {

        TipoMovimiento tipoMovimiento = tipoMovimientoRepo.findByCodigo(codigoTipoMovimiento)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de movimiento no encontrado con código: " + codigoTipoMovimiento));

        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        Compra compra = null;
        if (idCompra != null) {
            compra = compraRepo.findById(idCompra)
                    .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + idCompra));
        }

        if (!Boolean.TRUE.equals(tipoMovimiento.getEstado())) {
            throw new BusinessRuleException("El tipo de movimiento especificado se encuentra inactivo");
        }

        return registrarMovimientoInterno(
                idProducto,
                idAlmacen,
                usuario,
                tipoMovimiento,
                cantidad,
                motivo,
                referencia,
                compra);
    }

    private MovimientoInventario obtenerMovimientoExistente(Long id) {
        return movimientoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El movimiento de inventario no existe"));
    }

    private TipoMovimiento obtenerTipoMovimientoActivo(Integer idTipoMovimiento) {
        TipoMovimiento tipoMovimiento = tipoMovimientoRepo.findById(idTipoMovimiento)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de movimiento no encontrado con ID: " + idTipoMovimiento));

        if (!Boolean.TRUE.equals(tipoMovimiento.getEstado())) {
            throw new BusinessRuleException("El tipo de movimiento especificado se encuentra inactivo");
        }

        return tipoMovimiento;
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return usuarioRepo.findByCorreo(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario autenticado no encontrado"));
    }

    private void validarAutorizacionPorTipoMovimiento(TipoMovimiento tipoMovimiento) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdministrador = tieneAuthority(authentication, "ROLE_ADMINISTRADOR");
        boolean esAlmacenero = tieneAuthority(authentication, "ROLE_ALMACENERO");
        String codigo = tipoMovimiento.getCodigo();

        if (esAdministrador) {
            return;
        }

        if (esAlmacenero && ("AJUSTE_ENTRADA".equals(codigo) || "AJUSTE_SALIDA".equals(codigo))) {
            throw new AccessDeniedException("No tiene permisos para realizar ajustes de inventario");
        }
    }

    private boolean tieneAuthority(Authentication authentication, String authority) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}

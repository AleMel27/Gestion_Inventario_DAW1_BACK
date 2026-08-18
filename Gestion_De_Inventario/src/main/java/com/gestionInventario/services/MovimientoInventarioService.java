package com.gestionInventario.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.exception.BusinessRuleException;
import com.gestionInventario.exception.ResourceNotFoundException;
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

    public List<MovimientoInventario> listarTodos() {
        return movimientoRepo.findAll();
    }

    public MovimientoInventario obtenerPorId(Long id) {
        return movimientoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El movimiento de inventario no existe"));
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

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a cero");
        }

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));

        Almacen almacen = almacenRepo.findById(idAlmacen)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + idAlmacen));

        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        TipoMovimiento tipoMovimiento = tipoMovimientoRepo.findById(idTipoMovimiento)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de movimiento no encontrado con ID: " + idTipoMovimiento));

        if (!Boolean.TRUE.equals(tipoMovimiento.getEstado())) {
            throw new BusinessRuleException("El tipo de movimiento especificado se encuentra inactivo");
        }

        Compra compra = null;
        if (idCompra != null) {
            compra = compraRepo.findById(idCompra)
                    .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + idCompra));
        }

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

        return registrarMovimiento(
                idProducto,
                idAlmacen,
                idUsuario,
                tipoMovimiento.getIdTipoMovimiento(),
                cantidad,
                motivo,
                referencia,
                idCompra);
    }
}

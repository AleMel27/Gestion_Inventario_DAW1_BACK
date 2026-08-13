package com.gestionInventario.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return movimientoRepo.findById(id).orElse(null);
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
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + idProducto));

        Almacen almacen = almacenRepo.findById(idAlmacen)
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado con ID: " + idAlmacen));

        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        TipoMovimiento tipoMovimiento = tipoMovimientoRepo.findById(idTipoMovimiento)
                .orElseThrow(() -> new RuntimeException("Tipo de movimiento no encontrado con ID: " + idTipoMovimiento));

        if (!Boolean.TRUE.equals(tipoMovimiento.getEstado())) {
            throw new RuntimeException("El tipo de movimiento especificado se encuentra inactivo");
        }

        Compra compra = null;
        if (idCompra != null) {
            compra = compraRepo.findById(idCompra)
                    .orElseThrow(() -> new RuntimeException("Compra no encontrada con ID: " + idCompra));
        }

        Inventario inventario = inventarioRepo
                .findByProductoIdProductoAndAlmacenIdAlmacen(idProducto, idAlmacen)
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setProducto(producto);
                    nuevo.setAlmacen(almacen);
                    nuevo.setStockActual(BigDecimal.ZERO);
                    return nuevo;
                });

        BigDecimal stockAnterior = inventario.getStockActual();
        BigDecimal stockPosterior;

        // 3. Calcular el nuevo stock según el signoStock (+1, -1, 0)
        short signo = tipoMovimiento.getSignoStock();

        if (signo > 0) {
            // Entrada / Incremento
            stockPosterior = stockAnterior.add(cantidad);
        } else if (signo < 0) {
            // Salida / Decremento (Validación de Stock)
            if (stockAnterior.compareTo(cantidad) < 0) {
                throw new RuntimeException("Stock insuficiente en el almacén. Stock actual: " 
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
}
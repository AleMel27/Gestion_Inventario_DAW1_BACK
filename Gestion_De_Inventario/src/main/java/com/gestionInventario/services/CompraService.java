package com.gestionInventario.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.model.Almacen;
import com.gestionInventario.model.Compra;
import com.gestionInventario.model.DetalleCompra;
import com.gestionInventario.model.Inventario;
import com.gestionInventario.model.MovimientoInventario;
import com.gestionInventario.model.TipoComprobante;
import com.gestionInventario.model.TipoMovimiento;
import com.gestionInventario.repository.ICompraRepository;
import com.gestionInventario.repository.IDetalleCompraRepository;
import com.gestionInventario.repository.IInventarioRepository;
import com.gestionInventario.repository.IMovimientoInventarioRepository;
import com.gestionInventario.repository.ITipoComprobanteRepository;
import com.gestionInventario.repository.ITipoMovimientoRepository;

@Service
public class CompraService {

    @Autowired
    private ICompraRepository compraRepo;

    @Autowired
    private IDetalleCompraRepository detalleRepo;

    @Autowired
    private IInventarioRepository inventarioRepo;

    @Autowired
    private IMovimientoInventarioRepository movimientoRepo;

    @Autowired
    private ITipoMovimientoRepository tipoMovimientoRepo;

    @Autowired
    private ITipoComprobanteRepository tipoComprobanteRepo;

    public List<Compra> listarTodas() {
        return compraRepo.findAll();
    }

    public Compra obtenerPorId(Long id) {
        return compraRepo.findById(id).orElse(null);
    }


    @Transactional
    public Compra registrarCompra(Compra compra, Long idAlmacenDestino, List<DetalleCompra> detalles) {
        
        asignarTipoComprobanteExistente(compra);
        if (compra.getEstado() == null || compra.getEstado().trim().isEmpty()) {
            compra.setEstado("PENDIENTE");
        }
        compra.setTotal(BigDecimal.ZERO);
        Compra compraGuardada = compraRepo.save(compra);

        Almacen almacenDestino = new Almacen();
        almacenDestino.setIdAlmacen(idAlmacenDestino);

        TipoMovimiento tipoEntrada = tipoMovimientoRepo.findByCodigo("ENTRADA")
                .orElseThrow(() -> new RuntimeException("Tipo de movimiento ENTRADA no encontrado"));

        for (DetalleCompra detalle : detalles) {
            
            detalle.setCompra(compraGuardada);
            detalleRepo.save(detalle);

            BigDecimal stockAnterior = BigDecimal.ZERO;
            
            Inventario inventario = inventarioRepo
                .findByProductoIdProductoAndAlmacenIdAlmacen(detalle.getProducto().getIdProducto(), idAlmacenDestino)
                .orElse(null);

            if (inventario == null) {
                inventario = new Inventario();
                inventario.setProducto(detalle.getProducto());
                inventario.setAlmacen(almacenDestino);
                inventario.setStockActual(detalle.getCantidad());
            } else {
                stockAnterior = inventario.getStockActual();
                inventario.setStockActual(stockAnterior.add(detalle.getCantidad()));
            }
            inventarioRepo.save(inventario);

            BigDecimal stockPosterior = inventario.getStockActual();

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(detalle.getProducto());
            movimiento.setAlmacen(almacenDestino);
            movimiento.setUsuario(compraGuardada.getUsuario());
            movimiento.setCompra(compraGuardada);
            movimiento.setTipoMovimiento(tipoEntrada);
            movimiento.setCantidad(detalle.getCantidad());
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setStockPosterior(stockPosterior);
            movimiento.setMotivo("INGRESO POR COMPRA - COMPROBANTE N°: " + compraGuardada.getNumeroComprobante());
            movimiento.setReferencia(compraGuardada.getTipoComprobante().getCodigo());

            movimientoRepo.save(movimiento);
        }

        return compraRepo.findById(compraGuardada.getIdCompra()).orElse(compraGuardada);
    }

    private void asignarTipoComprobanteExistente(Compra compra) {
        if (compra.getTipoComprobante() == null
                || compra.getTipoComprobante().getIdTipoComprobante() == null) {
            throw new RuntimeException("El tipo de comprobante es obligatorio");
        }

        TipoComprobante tipoComprobante = tipoComprobanteRepo
                .findById(compra.getTipoComprobante().getIdTipoComprobante())
                .orElseThrow(() -> new RuntimeException("Tipo de comprobante no encontrado"));
        compra.setTipoComprobante(tipoComprobante);
    }
}

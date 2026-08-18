package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.response.MovimientoAlmacenDTO;
import com.gestionInventario.dtos.response.MovimientoCompraDTO;
import com.gestionInventario.dtos.response.MovimientoInventarioResDTO;
import com.gestionInventario.dtos.response.MovimientoProductoDTO;
import com.gestionInventario.dtos.response.MovimientoTipoDTO;
import com.gestionInventario.dtos.response.MovimientoUsuarioDTO;
import com.gestionInventario.model.Compra;
import com.gestionInventario.model.MovimientoInventario;

@Component
public class MovimientoInventarioMapper {

    public MovimientoInventarioResDTO convertirADto(MovimientoInventario movimiento) {
        if (movimiento == null) {
            return null;
        }

        return MovimientoInventarioResDTO.builder()
                .idMovimientoInventario(movimiento.getIdMovimiento())
                .producto(convertirProducto(movimiento))
                .almacen(convertirAlmacen(movimiento))
                .usuario(convertirUsuario(movimiento))
                .tipoMovimiento(convertirTipoMovimiento(movimiento))
                .compra(convertirCompra(movimiento.getCompra()))
                .cantidad(movimiento.getCantidad())
                .stockAnterior(movimiento.getStockAnterior())
                .stockPosterior(movimiento.getStockPosterior())
                .motivo(movimiento.getMotivo())
                .referencia(movimiento.getReferencia())
                .fechaMovimiento(movimiento.getFechaMovimiento())
                .build();
    }

    private MovimientoProductoDTO convertirProducto(MovimientoInventario movimiento) {
        if (movimiento.getProducto() == null) {
            return null;
        }
        return MovimientoProductoDTO.builder()
                .idProducto(movimiento.getProducto().getIdProducto())
                .codigo(movimiento.getProducto().getCodigo())
                .nombre(movimiento.getProducto().getNombre())
                .build();
    }

    private MovimientoAlmacenDTO convertirAlmacen(MovimientoInventario movimiento) {
        if (movimiento.getAlmacen() == null) {
            return null;
        }
        return MovimientoAlmacenDTO.builder()
                .idAlmacen(movimiento.getAlmacen().getIdAlmacen())
                .nombre(movimiento.getAlmacen().getNombre())
                .build();
    }

    private MovimientoUsuarioDTO convertirUsuario(MovimientoInventario movimiento) {
        if (movimiento.getUsuario() == null) {
            return null;
        }
        return MovimientoUsuarioDTO.builder()
                .idUsuario(movimiento.getUsuario().getIdUsuario())
                .nombres(movimiento.getUsuario().getNombres())
                .apellidos(movimiento.getUsuario().getApellidos())
                .build();
    }

    private MovimientoTipoDTO convertirTipoMovimiento(MovimientoInventario movimiento) {
        if (movimiento.getTipoMovimiento() == null) {
            return null;
        }
        return MovimientoTipoDTO.builder()
                .idTipoMovimiento(movimiento.getTipoMovimiento().getIdTipoMovimiento())
                .codigo(movimiento.getTipoMovimiento().getCodigo())
                .nombre(movimiento.getTipoMovimiento().getNombre())
                .signoStock(movimiento.getTipoMovimiento().getSignoStock())
                .build();
    }

    private MovimientoCompraDTO convertirCompra(Compra compra) {
        if (compra == null) {
            return null;
        }
        return MovimientoCompraDTO.builder()
                .idCompra(compra.getIdCompra())
                .numeroComprobante(compra.getNumeroComprobante())
                .build();
    }
}

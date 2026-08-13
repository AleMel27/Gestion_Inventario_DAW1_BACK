package com.gestionInventario.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.response.CompraResDTO;
import com.gestionInventario.dtos.response.DetalleCompraDTO;
import com.gestionInventario.model.Compra;
import com.gestionInventario.model.DetalleCompra;

@Component
public class CompraMapper {

    public CompraResDTO convertirADto(Compra compra, List<DetalleCompra> detalles) {
        if (compra == null) return null;

        List<DetalleCompraDTO> detallesDto = null;
        if (detalles != null) {
            detallesDto = detalles.stream()
                    .map(this::convertirDetalleADto)
                    .collect(Collectors.toList());
        }

        Short idTipoCompShort = null;
        if (compra.getTipoComprobante() != null && compra.getTipoComprobante().getIdTipoComprobante() != null) {
            idTipoCompShort = compra.getTipoComprobante().getIdTipoComprobante().shortValue();
        }

        return CompraResDTO.builder()
                .idCompra(compra.getIdCompra())
                .idProveedor(compra.getProveedor() != null ? compra.getProveedor().getIdProveedor() : null)
                .razonSocialProveedor(compra.getProveedor() != null ? compra.getProveedor().getRazonSocial() : null)
                .idUsuario(compra.getUsuario() != null ? compra.getUsuario().getIdUsuario() : null)
                .nombreUsuario(compra.getUsuario() != null 
                        ? compra.getUsuario().getNombres() + " " + compra.getUsuario().getApellidos() 
                        : null)
                .idTipoComprobante(idTipoCompShort) // <-- Se pasa la variable con el tipo Short
                .nombreTipoComprobante(compra.getTipoComprobante() != null ? compra.getTipoComprobante().getNombre() : null)
                .numeroComprobante(compra.getNumeroComprobante())
                .total(compra.getTotal())
                .estado(compra.getEstado())
                .observacion(compra.getObservacion())
                .fechaCompra(compra.getFechaCompra())
                .detalles(detallesDto)
                .build();
    }

    public DetalleCompraDTO convertirDetalleADto(DetalleCompra detalle) {
        if (detalle == null) return null;

        return DetalleCompraDTO.builder()
                .idDetalleCompra(detalle.getIdDetalleCompra())
                .idProducto(detalle.getProducto() != null ? detalle.getProducto().getIdProducto() : null)
                .codigoProducto(detalle.getProducto() != null ? detalle.getProducto().getCodigo() : null)
                .nombreProducto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null)
                .cantidad(detalle.getCantidad())
                .costoUnitario(detalle.getCostoUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}
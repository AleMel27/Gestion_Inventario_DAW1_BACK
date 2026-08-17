package com.gestionInventario.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.response.CompraResDTO;
import com.gestionInventario.dtos.response.CompraResDTO.AlmacenCompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO.ProveedorCompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO.TipoComprobanteCompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO.UsuarioCompraDTO;
import com.gestionInventario.dtos.response.CompraResumenDTO;
import com.gestionInventario.dtos.response.DetalleCompraDTO;
import com.gestionInventario.model.Compra;
import com.gestionInventario.model.DetalleCompra;

@Component
public class CompraMapper {

    public CompraResumenDTO convertirAResumenDto(Compra compra) {
        if (compra == null) return null;

        return CompraResumenDTO.builder()
                .idCompra(compra.getIdCompra())
                .proveedor(convertirProveedorADto(compra))
                .usuario(convertirUsuarioADto(compra))
                .almacen(convertirAlmacenADto(compra))
                .tipoComprobante(convertirTipoComprobanteADto(compra))
                .fechaCompra(compra.getFechaCompra())
                .numeroComprobante(compra.getNumeroComprobante())
                .total(compra.getTotal())
                .estado(compra.getEstado())
                .build();
    }

    public CompraResDTO convertirADto(Compra compra, List<DetalleCompra> detalles) {
        if (compra == null) return null;

        List<DetalleCompraDTO> detallesDto = null;
        if (detalles != null) {
            detallesDto = detalles.stream()
                    .map(this::convertirDetalleADto)
                    .collect(Collectors.toList());
        }

        return CompraResDTO.builder()
                .idCompra(compra.getIdCompra())
                .proveedor(convertirProveedorADto(compra))
                .usuario(convertirUsuarioADto(compra))
                .almacen(convertirAlmacenADto(compra))
                .tipoComprobante(convertirTipoComprobanteADto(compra))
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
                .subtotal(obtenerSubtotal(detalle))
                .build();
    }

    private java.math.BigDecimal obtenerSubtotal(DetalleCompra detalle) {
        if (detalle.getSubtotal() != null) {
            return detalle.getSubtotal();
        }
        if (detalle.getCantidad() == null || detalle.getCostoUnitario() == null) {
            return null;
        }
        return detalle.getCantidad()
                .multiply(detalle.getCostoUnitario())
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private ProveedorCompraDTO convertirProveedorADto(Compra compra) {
        if (compra.getProveedor() == null) return null;
        return ProveedorCompraDTO.builder()
                .idProveedor(compra.getProveedor().getIdProveedor())
                .ruc(compra.getProveedor().getRuc())
                .razonSocial(compra.getProveedor().getRazonSocial())
                .build();
    }

    private UsuarioCompraDTO convertirUsuarioADto(Compra compra) {
        if (compra.getUsuario() == null) return null;
        return UsuarioCompraDTO.builder()
                .idUsuario(compra.getUsuario().getIdUsuario())
                .nombres(compra.getUsuario().getNombres())
                .apellidos(compra.getUsuario().getApellidos())
                .build();
    }

    private AlmacenCompraDTO convertirAlmacenADto(Compra compra) {
        if (compra.getAlmacen() == null) return null;
        return AlmacenCompraDTO.builder()
                .idAlmacen(compra.getAlmacen().getIdAlmacen())
                .nombre(compra.getAlmacen().getNombre())
                .build();
    }

    private TipoComprobanteCompraDTO convertirTipoComprobanteADto(Compra compra) {
        if (compra.getTipoComprobante() == null) return null;
        return TipoComprobanteCompraDTO.builder()
                .idTipoComprobante(compra.getTipoComprobante().getIdTipoComprobante())
                .nombre(compra.getTipoComprobante().getNombre())
                .build();
    }
}

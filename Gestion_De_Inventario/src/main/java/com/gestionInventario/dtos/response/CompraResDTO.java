package com.gestionInventario.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResDTO {

    private Long idCompra;
    private ProveedorCompraDTO proveedor;
    private UsuarioCompraDTO usuario;
    private AlmacenCompraDTO almacen;
    private TipoComprobanteCompraDTO tipoComprobante;
    private String numeroComprobante;
    private BigDecimal total;
    private String estado;
    private String observacion;
    private LocalDateTime fechaCompra;
    private List<DetalleCompraDTO> detalles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProveedorCompraDTO {
        private Long idProveedor;
        private String ruc;
        private String razonSocial;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioCompraDTO {
        private Long idUsuario;
        private String nombres;
        private String apellidos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlmacenCompraDTO {
        private Long idAlmacen;
        private String nombre;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TipoComprobanteCompraDTO {
        private Short idTipoComprobante;
        private String nombre;
    }
}

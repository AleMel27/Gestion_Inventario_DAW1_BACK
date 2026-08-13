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
    private Long idProveedor;
    private String razonSocialProveedor;
    private Long idUsuario;
    private String nombreUsuario;
    private Short idTipoComprobante;
    private String nombreTipoComprobante;
    private String numeroComprobante;
    private BigDecimal total;
    private String estado;
    private String observacion;
    private LocalDateTime fechaCompra;
    private List<DetalleCompraDTO> detalles;
}
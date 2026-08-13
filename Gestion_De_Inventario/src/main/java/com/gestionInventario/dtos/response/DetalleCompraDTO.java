package com.gestionInventario.dtos.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCompraDTO {

    private Long idDetalleCompra;
    private Long idProducto;
    private String codigoProducto;
    private String nombreProducto;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
}
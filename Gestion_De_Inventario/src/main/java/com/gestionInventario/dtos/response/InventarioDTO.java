package com.gestionInventario.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {

    private Long idInventario;

    private Long idProducto;
    private String codigoProducto;
    private String nombreProducto;
    private BigDecimal stockMinimoProducto;

    private Long idAlmacen;
    private String nombreAlmacen;

    private BigDecimal stockActual;
    private Boolean alertaStockBajo;
    private LocalDateTime fechaActualizacion;
}
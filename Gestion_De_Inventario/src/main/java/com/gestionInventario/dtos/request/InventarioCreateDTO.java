package com.gestionInventario.dtos.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class InventarioCreateDTO {

    private Long idProducto;
    private Long idAlmacen;
    private BigDecimal stockActual;

}
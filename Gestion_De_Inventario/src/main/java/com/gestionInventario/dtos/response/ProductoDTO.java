package com.gestionInventario.dtos.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoDTO {

    private Long idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private BigDecimal stockMinimo;
    private Boolean estado;
    private ProductoCategoriaDTO categoria;
    private ProductoUnidadMedidaDTO unidadMedida;
}

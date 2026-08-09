package com.gestionInventario.dtos.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoCreateDTO {

    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer idUnidadMedida;
    private BigDecimal precioVenta;
    private BigDecimal stockMinimo;
    private Long idCategoria;
}

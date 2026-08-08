package com.gestionInventario.dtos;

import java.math.BigDecimal;
import com.gestionInventario.enums.UnidadMedida;
import lombok.Data;

@Data
public class ProductoDTO {
    
    private Long idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private UnidadMedida unidadMedida;
    private BigDecimal precioVenta;
    private BigDecimal stockMinimo;
    private Boolean estado;
    
    private Long idCategoria;
    private String nombreCategoria;
}
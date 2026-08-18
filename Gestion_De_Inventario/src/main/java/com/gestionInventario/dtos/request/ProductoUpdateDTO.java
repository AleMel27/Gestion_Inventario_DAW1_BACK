package com.gestionInventario.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no debe superar 500 caracteres")
    private String descripcion;

    @NotNull(message = "El ID de la unidad de medida es obligatorio")
    @Positive(message = "El ID de la unidad de medida debe ser mayor a cero")
    private Integer idUnidadMedida;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.000", inclusive = true, message = "El stock mínimo no puede ser negativo")
    private BigDecimal stockMinimo;

    @NotNull(message = "El ID de la categoría es obligatorio")
    @Positive(message = "El ID de la categoría debe ser mayor a cero")
    private Long idCategoria;
}

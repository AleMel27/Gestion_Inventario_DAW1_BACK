package com.gestionInventario.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InventarioCreateDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    @Positive(message = "El ID del producto debe ser mayor a cero")
    private Long idProducto;

    @NotNull(message = "El ID del almacén es obligatorio")
    @Positive(message = "El ID del almacén debe ser mayor a cero")
    private Long idAlmacen;

    @NotNull(message = "El stock actual es obligatorio")
    @DecimalMin(value = "0.000", inclusive = true, message = "El stock actual no puede ser negativo")
    private BigDecimal stockActual;

}

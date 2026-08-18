package com.gestionInventario.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioUpdateDTO {

    @NotNull(message = "El stock actual es obligatorio")
    @DecimalMin(value = "0.000", inclusive = true, message = "El stock actual no puede ser negativo")
    private BigDecimal stockActual;
}

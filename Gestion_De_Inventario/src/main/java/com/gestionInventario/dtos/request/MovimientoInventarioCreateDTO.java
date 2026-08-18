package com.gestionInventario.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MovimientoInventarioCreateDTO {

    @NotNull
    @Positive
    private Long idProducto;

    @NotNull
    @Positive
    private Long idAlmacen;

    @NotNull
    @Positive
    private Integer idTipoMovimiento;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal cantidad;

    @NotBlank
    @Size(max = 255)
    private String motivo;

    @Size(max = 100)
    private String referencia;
}

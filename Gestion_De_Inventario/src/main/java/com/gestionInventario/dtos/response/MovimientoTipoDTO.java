package com.gestionInventario.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoTipoDTO {
    private Integer idTipoMovimiento;
    private String codigo;
    private String nombre;
    private Short signoStock;
}

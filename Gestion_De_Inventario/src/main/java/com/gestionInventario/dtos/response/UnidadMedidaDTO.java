package com.gestionInventario.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadMedidaDTO {

    private Integer idUnidadMedida;
    private String codigo;
    private String nombre;
    private String abreviatura;
    private String descripcion;
    private Boolean permiteDecimales;
    private Boolean estado;
}
package com.gestionInventario.dtos.response;

import lombok.Data;

@Data
public class AlmacenDTO {

    private Long idAlmacen;
    private String nombre;
    private String ubicacion;
    private String descripcion;
    private Boolean estado;
}
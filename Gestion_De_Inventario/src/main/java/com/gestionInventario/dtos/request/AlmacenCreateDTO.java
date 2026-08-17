package com.gestionInventario.dtos.request;

import lombok.Data;

@Data
public class AlmacenCreateDTO {

    private String nombre;
    private String ubicacion;
    private String descripcion;
}
package com.gestionInventario.dtos.response;

import lombok.Data;

@Data
public class CategoriaDTO {

    private Long idCategoria;
    private String nombre;
    private String descripcion;
    private Boolean estado;
}

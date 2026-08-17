package com.gestionInventario.dtos.request;


import lombok.Data;

@Data
public class UsuarioUpdateDTO {
	
    private String nombres;
    private String apellidos;
    private String correo;
    private Short idRol;
}

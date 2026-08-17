package com.gestionInventario.dtos.request;



import lombok.Data;

@Data
public class UsuarioCreateDTO {
	
    private String nombres;
    private String apellidos;
    private String correo;
    private String passwordHash;
    private Short idrol;
}

package com.gestionInventario.dtos.request;

import lombok.Data;

@Data
public class ProveedorCreateDTO {
	
	private String ruc;
	private String razonSocial;
	private String telefono;
	private String correo;
	private String direccion;
	
}

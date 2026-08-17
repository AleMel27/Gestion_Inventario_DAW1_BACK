package com.gestionInventario.dtos.response;

import lombok.Data;

@Data
public class ProveedorDTO {
	
	private Long idProveedor;
	private String ruc;
	private String razonSocial;
	private String telefono;
	private String correo;
	private String direccion;
	private Boolean estado;
	
}

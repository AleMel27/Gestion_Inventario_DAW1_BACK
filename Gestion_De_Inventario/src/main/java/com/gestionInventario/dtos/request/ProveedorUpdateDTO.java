package com.gestionInventario.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorUpdateDTO {

	@Size(max = 20, message = "El teléfono no debe superar 20 caracteres")
	private String telefono;

	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "El correo debe tener un formato válido")
	@Size(max = 150, message = "El correo no debe superar 150 caracteres")
	private String correo;

	@Size(max = 255, message = "La dirección no debe superar 255 caracteres")
	private String direccion;
	
}

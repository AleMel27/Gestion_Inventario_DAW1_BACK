package com.gestionInventario.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UsuarioUpdateDTO {

    @NotBlank
    @Size(max = 100)
    private String nombres;

    @NotBlank
    @Size(max = 100)
    private String apellidos;

    @NotBlank
    @Email
    @Size(max = 150)
    private String correo;

    @Size(max = 255)
    private String password;

    @NotNull
    @Positive
    private Short idRol;
}

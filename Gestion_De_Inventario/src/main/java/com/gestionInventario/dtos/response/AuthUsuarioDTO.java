package com.gestionInventario.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUsuarioDTO {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private AuthRolDTO rol;
    private Boolean estado;
}

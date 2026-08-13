package com.gestionInventario.dtos.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private Boolean estado;
    private Short idRol;
    private String nombreRol;
    private LocalDateTime fechaCreacion;
}
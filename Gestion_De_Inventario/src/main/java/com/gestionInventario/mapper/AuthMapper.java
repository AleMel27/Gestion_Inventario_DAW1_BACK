package com.gestionInventario.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.response.AuthRolDTO;
import com.gestionInventario.dtos.response.AuthResponseDTO;
import com.gestionInventario.dtos.response.AuthUsuarioDTO;
import com.gestionInventario.model.Usuario;

@Component
public class AuthMapper {

    public AuthResponseDTO convertirAAuthResponse(Usuario usuario, String token, Instant expiration) {
        if (usuario == null) {
            return null;
        }

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiration(expiration)
                .usuario(convertirUsuario(usuario))
                .build();
    }

    private AuthUsuarioDTO convertirUsuario(Usuario usuario) {
        return AuthUsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .rol(convertirRol(usuario))
                .estado(usuario.getEstado())
                .build();
    }

    private AuthRolDTO convertirRol(Usuario usuario) {
        if (usuario.getRol() == null) {
            return null;
        }

        return AuthRolDTO.builder()
                .idRol(usuario.getRol().getIdRol())
                .nombre(usuario.getRol().getNombre())
                .build();
    }
}

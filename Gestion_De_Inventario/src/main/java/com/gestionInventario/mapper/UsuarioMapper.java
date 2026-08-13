package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.response.UsuarioDTO;
import com.gestionInventario.model.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioDTO convertirADto(Usuario usuario) {
        if (usuario == null) return null;

        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .estado(usuario.getEstado())
                .idRol(usuario.getRol() != null ? usuario.getRol().getIdRol() : null)
                .nombreRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null)
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}
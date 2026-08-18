package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.request.UsuarioCreateDTO;
import com.gestionInventario.dtos.request.UsuarioUpdateDTO;
import com.gestionInventario.dtos.response.UsuarioDTO;
import com.gestionInventario.dtos.response.UsuarioRolDTO;
import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioDTO convertirADto(Usuario usuario) {
    	UsuarioDTO dto = new UsuarioDTO();
    	dto.setIdUsuario(usuario.getIdUsuario());
    	dto.setNombres(usuario.getNombres());
    	dto.setApellidos(usuario.getApellidos());
    	dto.setCorreo(usuario.getCorreo());
    	dto.setEstado(usuario.getEstado());
        
        if (usuario.getRol() != null) {
            UsuarioRolDTO rolDTO = new UsuarioRolDTO();
            rolDTO.setIdRol(usuario.getRol().getIdRol());
            rolDTO.setNombre(usuario.getRol().getNombre());
            dto.setRol(rolDTO);
        }
        
        return dto;
    }
    
    public Usuario covertirDtoCreate(UsuarioCreateDTO dto) {
    	Usuario usuario = new Usuario();
    	usuario.setNombres(dto.getNombres());
    	usuario.setApellidos(dto.getApellidos());
    	usuario.setCorreo(dto.getCorreo());
    	usuario.setPasswordHash(dto.getPassword());
    	usuario.setEstado(true);
    	
        if (dto.getIdrol() != null) {
            Rol rol = new Rol();
            rol.setIdRol(dto.getIdrol());
            usuario.setRol(rol);
        }
        
        return usuario;
    }
    
    public Usuario convertirDtoUpdate(UsuarioUpdateDTO dto) {
    	Usuario usuario = new Usuario();
    	usuario.setNombres(dto.getNombres());
    	usuario.setApellidos(dto.getApellidos());
    	usuario.setCorreo(dto.getCorreo());
    	usuario.setEstado(true);
    	
        if (dto.getIdRol() != null) {
            Rol rol = new Rol();
            rol.setIdRol(dto.getIdRol());
            usuario.setRol(rol);
        }
        
        return usuario;
    }
}

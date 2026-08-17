package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.dtos.response.RolDTO;
import com.gestionInventario.model.Rol;
import com.gestionInventario.repository.IRolRepository;

@Service
public class RolService {

    @Autowired
    private IRolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<RolDTO> listarTodos() {
        return rolRepository.findAll()
                .stream()
                .map(this::convertirADto)
                .toList();
    }

    private RolDTO convertirADto(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombre(rol.getNombre());
        return dto;
    }
}
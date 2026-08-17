package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.request.AlmacenCreateDTO;
import com.gestionInventario.dtos.request.AlmacenUpdateDTO;
import com.gestionInventario.dtos.response.AlmacenDTO;
import com.gestionInventario.model.Almacen;

@Component
public class AlmacenMapper {

    public AlmacenDTO convertirADto(Almacen almacen) {
        if (almacen == null) {
            return null;
        }
        
        AlmacenDTO dto = new AlmacenDTO();
        dto.setIdAlmacen(almacen.getIdAlmacen());
        dto.setNombre(almacen.getNombre());
        dto.setUbicacion(almacen.getUbicacion());
        dto.setDescripcion(almacen.getDescripcion());
        dto.setEstado(almacen.getEstado());
        
        return dto;
    }

    public Almacen convertirAEntidad(AlmacenCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Almacen almacen = new Almacen();
        almacen.setNombre(dto.getNombre());
        almacen.setUbicacion(dto.getUbicacion());
        almacen.setDescripcion(dto.getDescripcion());
        almacen.setEstado(true);

        return almacen;
    }

    public Almacen convertirAEntidad(AlmacenUpdateDTO dto) {
        if (dto == null) {
            return null;
        }

        Almacen almacen = new Almacen();
        almacen.setNombre(dto.getNombre());
        almacen.setUbicacion(dto.getUbicacion());
        almacen.setDescripcion(dto.getDescripcion());

        return almacen;
    }
}
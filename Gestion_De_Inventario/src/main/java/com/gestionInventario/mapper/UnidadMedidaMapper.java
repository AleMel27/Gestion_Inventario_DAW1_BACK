package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.response.UnidadMedidaDTO;
import com.gestionInventario.model.UnidadMedida;

@Component
public class UnidadMedidaMapper {

    public UnidadMedidaDTO convertirADto(UnidadMedida entidad) {
        if (entidad == null) return null;

        return UnidadMedidaDTO.builder()
                .idUnidadMedida(entidad.getIdUnidadMedida())
                .codigo(entidad.getCodigo())
                .nombre(entidad.getNombre())
                .abreviatura(entidad.getAbreviatura())
                .descripcion(entidad.getDescripcion())
                .permiteDecimales(entidad.getPermiteDecimales())
                .estado(entidad.getEstado())
                .build();
    }

    public UnidadMedida convertirAEntidad(UnidadMedidaDTO dto) {
        if (dto == null) return null;

        UnidadMedida entidad = new UnidadMedida();
        entidad.setIdUnidadMedida(dto.getIdUnidadMedida());
        entidad.setCodigo(dto.getCodigo());
        entidad.setNombre(dto.getNombre());
        entidad.setAbreviatura(dto.getAbreviatura());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setPermiteDecimales(dto.getPermiteDecimales() != null ? dto.getPermiteDecimales() : false);
        entidad.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        return entidad;
    }
}
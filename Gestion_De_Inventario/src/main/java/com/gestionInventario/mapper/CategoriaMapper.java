package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.request.CategoriaCreateDTO;
import com.gestionInventario.dtos.request.CategoriaUpdateDTO;
import com.gestionInventario.dtos.response.CategoriaDTO;
import com.gestionInventario.model.Categoria;

@Component
public class CategoriaMapper {

    public CategoriaDTO convertirADto(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setEstado(categoria.getEstado());
        return dto;
    }

    public Categoria convertirAEntidad(CategoriaCreateDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setEstado(true);
        return categoria;
    }

    public Categoria convertirAEntidad(CategoriaUpdateDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }
}

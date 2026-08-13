package com.gestionInventario.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.dtos.response.UnidadMedidaDTO;
import com.gestionInventario.mapper.UnidadMedidaMapper;
import com.gestionInventario.model.UnidadMedida;
import com.gestionInventario.repository.IUnidadMedidaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadMedidaService {

    private final IUnidadMedidaRepository repository;
    private final UnidadMedidaMapper mapper;

    @Transactional(readOnly = true)
    public List<UnidadMedidaDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::convertirADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UnidadMedidaDTO obtenerPorId(Integer id) {
        UnidadMedida unidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada con ID: " + id));
        return mapper.convertirADto(unidad);
    }

    @Transactional
    public UnidadMedidaDTO guardar(UnidadMedidaDTO dto) {
        UnidadMedida entidad = mapper.convertirAEntidad(dto);
        UnidadMedida guardado = repository.save(entidad);
        return mapper.convertirADto(guardado);
    }

    @Transactional
    public UnidadMedidaDTO actualizar(Integer id, UnidadMedidaDTO dto) {
        UnidadMedida existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada con ID: " + id));

        existente.setCodigo(dto.getCodigo());
        existente.setNombre(dto.getNombre());
        existente.setAbreviatura(dto.getAbreviatura());
        existente.setDescripcion(dto.getDescripcion());
        if (dto.getPermiteDecimales() != null) existente.setPermiteDecimales(dto.getPermiteDecimales());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());

        return mapper.convertirADto(repository.save(existente));
    }
}
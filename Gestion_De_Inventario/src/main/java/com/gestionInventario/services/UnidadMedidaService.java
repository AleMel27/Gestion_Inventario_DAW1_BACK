package com.gestionInventario.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page; // AGREGADO
import org.springframework.data.domain.Pageable; // AGREGADO
import org.springframework.data.jpa.domain.Specification; // AGREGADO
import org.springframework.util.StringUtils; // AGREGADO

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

	// =========================================================================
	// MÉTODO AGREGADO: Paginado dinámico con filtro por nombre, código o
	// abreviatura ---- HECHO
	// =========================================================================
	@Transactional(readOnly = true)
	public Page<UnidadMedidaDTO> listarConFiltros(String buscar, Pageable pageable) {
		Specification<UnidadMedida> spec = (root, query, cb) -> cb.conjunction();

		if (StringUtils.hasText(buscar)) {
			String filtro = "%" + buscar.trim().toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.or(cb.like(cb.lower(root.get("nombre")), filtro),
					cb.like(cb.lower(root.get("codigo")), filtro), cb.like(cb.lower(root.get("abreviatura")), filtro)));
		}

		return repository.findAll(spec, pageable).map(mapper::convertirADto);
	}
	// =========================================================================

	@Transactional(readOnly = true)
	public List<UnidadMedidaDTO> listarTodos() {
		return repository.findAll().stream().map(mapper::convertirADto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public UnidadMedidaDTO obtenerPorId(Integer id) {
		UnidadMedida unidad = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada con ID: " + id));
		return mapper.convertirADto(unidad);
	}
}
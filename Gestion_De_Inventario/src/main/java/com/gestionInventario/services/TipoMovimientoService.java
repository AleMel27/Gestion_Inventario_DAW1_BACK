package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.dtos.response.TipoMovimientoDTO;
import com.gestionInventario.model.TipoMovimiento;
import com.gestionInventario.repository.ITipoMovimientoRepository;

@Service
public class TipoMovimientoService {

	@Autowired
	private ITipoMovimientoRepository repo;
	
	@Transactional(readOnly = true)
	public List<TipoMovimientoDTO> listarTodos(){
		return repo.findAll()
				.stream()
				.map(this::convertirADto)
				.toList();
	}
	
	private TipoMovimientoDTO convertirADto(TipoMovimiento movimiento) {
		TipoMovimientoDTO dto = new TipoMovimientoDTO();
		dto.setIdTipoMovimiento(movimiento.getIdTipoMovimiento());
		dto.setNombre(movimiento.getNombre());
		return dto;
	}
}

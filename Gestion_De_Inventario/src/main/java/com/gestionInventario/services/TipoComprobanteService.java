package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.dtos.response.TipoComprobanteDTO;
import com.gestionInventario.model.TipoComprobante;
import com.gestionInventario.repository.ITipoComprobanteRepository;

@Service
public class TipoComprobanteService {
	
	@Autowired
	private ITipoComprobanteRepository repo;
	
	@Transactional(readOnly = true)
	public List<TipoComprobanteDTO> listarTodos(){
		return repo.findAll()
				.stream()
				.map(this::convertirADto)
				.toList();
	}
	
	private TipoComprobanteDTO convertirADto(TipoComprobante tipoComprobante) {
		TipoComprobanteDTO dto = new TipoComprobanteDTO();
		dto.setIdTipoComprobante(tipoComprobante.getIdTipoComprobante());
		dto.setNombre(tipoComprobante.getNombre());
		return dto;
	}
}

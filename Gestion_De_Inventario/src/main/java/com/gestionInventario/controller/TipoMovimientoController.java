package com.gestionInventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestionInventario.dtos.response.TipoMovimientoDTO;
import com.gestionInventario.services.TipoMovimientoService;

@RestController
@RequestMapping("/api/movimiento")
public class TipoMovimientoController {

	@Autowired
	private TipoMovimientoService service;
	
	@GetMapping
	public ResponseEntity<List<TipoMovimientoDTO>> listarTodos(){
		List<TipoMovimientoDTO> movimiento = service.listarTodos();
		return new ResponseEntity<>(movimiento, HttpStatus.OK);
	}
	
}

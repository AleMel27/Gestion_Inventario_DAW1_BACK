package com.gestionInventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestionInventario.dtos.response.TipoComprobanteDTO;
import com.gestionInventario.services.TipoComprobanteService;

@RestController
@RequestMapping("/api/comprobante")
public class TipoComprobanteController {

	@Autowired
	private TipoComprobanteService service;
	
	@GetMapping
	public ResponseEntity<List<TipoComprobanteDTO>> listarTodos(){
		List<TipoComprobanteDTO> comprobante = service.listarTodos();
		return new ResponseEntity<>(comprobante, HttpStatus.OK);
	}
}

package com.gestionInventario.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.dtos.request.CategoriaCreateDTO;
import com.gestionInventario.dtos.request.CategoriaUpdateDTO;
import com.gestionInventario.dtos.response.CategoriaDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.mapper.CategoriaMapper;
import com.gestionInventario.model.Categoria;
import com.gestionInventario.services.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

	@Autowired
	private CategoriaService service;

	@Autowired
	private CategoriaMapper mapper;

	@GetMapping
	public ResponseEntity<PageDTO<CategoriaDTO>> listar(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "true") Boolean estado) {
		int size = 10;
		int pageIndex = Math.max(page - 1, 0);
		Page<Categoria> categorias = service.listarPorEstado(estado, PageRequest.of(pageIndex, size));
		List<CategoriaDTO> dtos = categorias.getContent()
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());
		PageDTO<CategoriaDTO> response = new PageDTO<>(
				dtos,
				categorias.getTotalElements(),
				categorias.getTotalPages(),
				categorias.getNumber() + 1,
				categorias.getSize());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Long id) {
		Categoria categoria = service.obtenerPorId(id);
		if (categoria == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(mapper.convertirADto(categoria));
	}

	@PostMapping
	public ResponseEntity<CategoriaDTO> registrar(@RequestBody CategoriaCreateDTO dto) {
		Categoria categoria = mapper.convertirAEntidad(dto);
		Categoria registrada = service.registrar(categoria);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertirADto(registrada));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoriaDTO> actualizar(
			@PathVariable Long id,
			@RequestBody CategoriaUpdateDTO dto) {
		Categoria categoria = mapper.convertirAEntidad(dto);
		Categoria actualizado = service.actualizar(id, categoria);
		if (actualizado == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(mapper.convertirADto(actualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		boolean eliminado = service.eliminar(id);
		if (!eliminado) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/reactivar")
	public ResponseEntity<Void> reactivar(@PathVariable Long id) {
		boolean reactivado = service.reactivar(id);
		if (!reactivado) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
	}
}

package com.gestionInventario.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.dtos.request.AlmacenCreateDTO;
import com.gestionInventario.dtos.request.AlmacenUpdateDTO;
import com.gestionInventario.dtos.response.AlmacenDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.mapper.AlmacenMapper;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.services.AlmacenService;

@RestController
@RequestMapping("/api/almacen")
@CrossOrigin(origins = "*")
public class AlmacenController {

	@Autowired
	private AlmacenService service;

	@Autowired
	private AlmacenMapper mapper;

	@GetMapping
	public ResponseEntity<PageDTO<AlmacenDTO>> listar(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "true") Boolean estado,
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String ubicacion) {
		int size = 10;
		int pageIndex = Math.max(page - 1, 0);
		Page<Almacen> almacenes = service.listarConFiltros(
				estado,
				nombre,
				ubicacion,
				PageRequest.of(pageIndex, size));
		List<AlmacenDTO> dtos = almacenes.getContent()
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());
		PageDTO<AlmacenDTO> response = new PageDTO<>(
				dtos,
				almacenes.getTotalElements(),
				almacenes.getTotalPages(),
				almacenes.getNumber() + 1,
				almacenes.getSize());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AlmacenDTO> obtenerPorId(@PathVariable Long id) {
		Almacen almacen = service.obtenerPorId(id);
		if (almacen == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(mapper.convertirADto(almacen));
	}

	@PostMapping
	public ResponseEntity<AlmacenDTO> registrar(@RequestBody AlmacenCreateDTO dto) {
		Almacen almacen = mapper.convertirAEntidad(dto);
		Almacen registrado = service.registrar(almacen);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertirADto(registrado));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AlmacenDTO> actualizar(
			@PathVariable Long id,
			@RequestBody AlmacenUpdateDTO dto) {
		Almacen almacenExistente = service.obtenerPorId(id);
		if (almacenExistente == null) {
			return ResponseEntity.notFound().build();
		}
		mapper.actualizarEntidadDesdeDto(dto, almacenExistente);
		Almacen actualizado = service.actualizar(id, almacenExistente);
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
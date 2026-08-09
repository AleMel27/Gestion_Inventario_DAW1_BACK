package com.gestionInventario.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.dtos.request.ProductoCreateDTO;
import com.gestionInventario.dtos.request.ProductoUpdateDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.dtos.response.ProductoDTO;
import com.gestionInventario.mapper.ProductoMapper;
import com.gestionInventario.model.Producto;
import com.gestionInventario.services.ProductoService;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

	@Autowired
	private ProductoService service;

	@Autowired
	private ProductoMapper mapper;

	@GetMapping
	public ResponseEntity<PageDTO<ProductoDTO>> listar(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "true") Boolean estado) {
		int size = 10;
		int pageIndex = Math.max(page - 1, 0);
		Page<Producto> productos = service.listarPorEstado(estado, PageRequest.of(pageIndex, size));
		List<ProductoDTO> dtos = productos.getContent()
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());
		PageDTO<ProductoDTO> response = new PageDTO<>(
				dtos,
				productos.getTotalElements(),
				productos.getTotalPages(),
				productos.getNumber() + 1,
				productos.getSize());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
		Producto producto = service.obtenerPorId(id);
		if (producto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(mapper.convertirADto(producto));
	}

	@PostMapping
	public ResponseEntity<ProductoDTO> registrar(@RequestBody ProductoCreateDTO dto) {
		Producto producto = mapper.convertirAEntidad(dto);
		Producto registrado = service.registrar(producto);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertirADto(registrado));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductoDTO> actualizar(
			@PathVariable Long id,
			@RequestBody ProductoUpdateDTO dto) {
		Producto producto = mapper.convertirAEntidad(dto);
		Producto actualizado = service.actualizar(id, producto);
		if (actualizado == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(mapper.convertirADto(actualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}

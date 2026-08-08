package com.gestionInventario.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.dtos.ProductoDTO;
import com.gestionInventario.model.Producto;
import com.gestionInventario.services.ProductoService;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

	@Autowired
	private ProductoService service;

	@GetMapping
	public ResponseEntity<List<ProductoDTO>> listar() {
		List<ProductoDTO> dtos = service.listarTodos()
				.stream()
				.map(this::convertirADto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
		Producto producto = service.obtenerPorId(id);
		if (producto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(convertirADto(producto));
	}

	@PostMapping
	public ResponseEntity<ProductoDTO> registrar(@RequestBody ProductoDTO dto) {
		Producto producto = convertirAEntidad(dto);
		Producto registrado = service.registrar(producto);
		return ResponseEntity.status(HttpStatus.CREATED).body(convertirADto(registrado));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @RequestBody ProductoDTO dto) {
		Producto producto = convertirAEntidad(dto);
		Producto actualizado = service.actualizar(id, producto);
		if (actualizado == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(convertirADto(actualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	
	// ==========================================
	// MÉTODOS DE CONVERSIÓN (MAPPERS MANUALES)
	// ==========================================
	
	private ProductoDTO convertirADto(Producto p) {
		ProductoDTO dto = new ProductoDTO();
		dto.setIdProducto(p.getIdProducto());
		dto.setCodigo(p.getCodigo());
		dto.setNombre(p.getNombre());
		dto.setDescripcion(p.getDescripcion());
		dto.setUnidadMedida(p.getUnidadMedida());
		dto.setPrecioVenta(p.getPrecioVenta());
		dto.setStockMinimo(p.getStockMinimo());
		dto.setEstado(p.getEstado());
		
		if (p.getCategoria() != null) {
			dto.setIdCategoria(p.getCategoria().getIdCategoria());
			dto.setNombreCategoria(p.getCategoria().getNombre());
		}
		return dto;
	}

	private Producto convertirAEntidad(ProductoDTO dto) {
		Producto p = new Producto();
		p.setIdProducto(dto.getIdProducto());
		p.setCodigo(dto.getCodigo());
		p.setNombre(dto.getNombre());
		p.setDescripcion(dto.getDescripcion());
		p.setUnidadMedida(dto.getUnidadMedida());
		p.setPrecioVenta(dto.getPrecioVenta());
		p.setStockMinimo(dto.getStockMinimo());
		p.setEstado(dto.getEstado());
		
		if (dto.getIdCategoria() != null) {
			com.gestionInventario.model.Categoria c = new com.gestionInventario.model.Categoria();
			c.setIdCategoria(dto.getIdCategoria());
			p.setCategoria(c);
		}
		return p;
	}
}
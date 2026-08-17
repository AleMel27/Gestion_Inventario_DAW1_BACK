package com.gestionInventario.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.dtos.request.InventarioCreateDTO;
import com.gestionInventario.dtos.request.InventarioUpdateDTO;
import com.gestionInventario.dtos.response.InventarioDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.mapper.InventarioMapper;
import com.gestionInventario.model.Inventario;
import com.gestionInventario.services.InventarioService;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

	@Autowired
	private InventarioService service;

	@Autowired
	private InventarioMapper mapper;

	// =========================================================================
	// CRUD BÁSICO Y CONSULTAS PRINCIPALES
	// =========================================================================

	@GetMapping
	public ResponseEntity<PageDTO<InventarioDTO>> listar(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) Long idAlmacen,
			@RequestParam(required = false) String nombreProducto) {

		int size = 10;
		int pageIndex = Math.max(page - 1, 0);

		Page<Inventario> pagina = service.listarConFiltros(
				idAlmacen,
				nombreProducto,
				PageRequest.of(pageIndex, size));

		List<InventarioDTO> dtos = pagina.getContent()
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());

		PageDTO<InventarioDTO> response = new PageDTO<>(
				dtos,
				pagina.getTotalElements(),
				pagina.getTotalPages(),
				pagina.getNumber() + 1,
				pagina.getSize());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<InventarioDTO> obtenerPorId(@PathVariable Long id) {
		Inventario inventario = service.obtenerPorId(id);
		if (inventario == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(mapper.convertirADto(inventario));
	}

	@PostMapping
	public ResponseEntity<InventarioDTO> registrar(@Valid @RequestBody InventarioCreateDTO dto) {
		Inventario inventario = mapper.convertirAEntidad(dto);
		Inventario registrado = service.registrar(inventario);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertirADto(registrado));
	}

	@PutMapping("/{id}")
	public ResponseEntity<InventarioDTO> actualizar(
			@PathVariable Long id,
			@Valid @RequestBody InventarioUpdateDTO dto) {

		Inventario inventarioExistente = service.obtenerPorId(id);
		if (inventarioExistente == null) {
			return ResponseEntity.notFound().build();
		}

		mapper.actualizarEntidadDesdeDto(dto, inventarioExistente);

		Inventario actualizado = service.actualizar(id, inventarioExistente);

		return ResponseEntity.ok(mapper.convertirADto(actualizado));
	}

	// =========================================================================
	// CONSULTAS POR ALMACÉN Y PRODUCTO
	// =========================================================================

	// Obtener todo el inventario de un almacén (sin paginar o paginado con Query Param)
	@GetMapping("/almacen/{idAlmacen}")
	public ResponseEntity<List<InventarioDTO>> listarPorAlmacen(@PathVariable Long idAlmacen) {
		List<InventarioDTO> dtos = service.listarPorAlmacen(idAlmacen)
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/almacen/{idAlmacen}/paginado")
	public ResponseEntity<PageDTO<InventarioDTO>> listarPorAlmacenPaginado(
			@PathVariable Long idAlmacen,
			@RequestParam(defaultValue = "1") int page) {

		int size = 10;
		int pageIndex = Math.max(page - 1, 0);

		Page<Inventario> pagina = service.listarPorAlmacenPaginado(idAlmacen, PageRequest.of(pageIndex, size));

		List<InventarioDTO> dtos = pagina.getContent()
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());

		PageDTO<InventarioDTO> response = new PageDTO<>(
				dtos,
				pagina.getTotalElements(),
				pagina.getTotalPages(),
				pagina.getNumber() + 1,
				pagina.getSize());

		return ResponseEntity.ok(response);
	}

	// Obtener el registro específico de un producto en un almacén
	@GetMapping("/producto/{idProducto}/almacen/{idAlmacen}")
	public ResponseEntity<InventarioDTO> obtenerPorProductoYAlmacen(
			@PathVariable Long idProducto,
			@PathVariable Long idAlmacen) {

		Inventario inventario = service.obtenerPorProductoYAlmacen(idProducto, idAlmacen);
		return ResponseEntity.ok(mapper.convertirADto(inventario));
	}

	// Consultar únicamente el número del stock actual
	@GetMapping("/producto/{idProducto}/almacen/{idAlmacen}/stock")
	public ResponseEntity<BigDecimal> consultarStockActual(
			@PathVariable Long idProducto,
			@PathVariable Long idAlmacen) {

		BigDecimal stock = service.consultarStockActual(idProducto, idAlmacen);
		return ResponseEntity.ok(stock);
	}

	// =========================================================================
	// ALERTAS Y OPERACIONES ESPECIALES
	// =========================================================================

	@GetMapping("/alertas")
	public ResponseEntity<List<InventarioDTO>> obtenerAlertas() {
		List<InventarioDTO> alertas = service.obtenerAlertasStockBajo()
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());

		return ResponseEntity.ok(alertas);
	}

	@GetMapping("/alertas/almacen/{idAlmacen}")
	public ResponseEntity<List<InventarioDTO>> obtenerAlertasPorAlmacen(@PathVariable Long idAlmacen) {
		List<InventarioDTO> alertas = service.obtenerAlertasStockBajoPorAlmacen(idAlmacen)
				.stream()
				.map(mapper::convertirADto)
				.collect(Collectors.toList());

		return ResponseEntity.ok(alertas);
	}
}
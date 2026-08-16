package com.gestionInventario.controller;

import java.util.List;
import java.util.stream.Collectors;




import org.springframework.data.domain.Pageable; // IMPORT AGREGADO
import org.springframework.data.domain.Sort; // IMPORT AGREGADO

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.dtos.response.InventarioDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.mapper.InventarioMapper;
import com.gestionInventario.model.Inventario;
import com.gestionInventario.services.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService service;
    private final InventarioMapper mapper;

 // =========================================================================
    // AGREGADO: Endpoint principal para listar con paginado y filtros opcionales ---- HECHO 
    // ========================================================================= 
    @GetMapping
    public ResponseEntity<PageDTO<InventarioDTO>> listarPaginado(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idInventario") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Long idAlmacen,
            @RequestParam(required = false) String nombreProducto) {

        int pageIndex = Math.max(page - 1, 0);

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageIndex, size, sort);

        Page<Inventario> pagina = service.listarConFiltros(idAlmacen, nombreProducto, pageable);

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

    // Mantenemos tu endpoint previo por ID de almacén para retrocompatibilidad
    @GetMapping("/almacen/{idAlmacen}")
    public ResponseEntity<PageDTO<InventarioDTO>> listarPorAlmacen(
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

    @GetMapping("/alertas")
    public ResponseEntity<List<InventarioDTO>> obtenerAlertas() {
        List<InventarioDTO> alertas = service.obtenerAlertasStockBajo()
                .stream()
                .map(mapper::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(alertas);
    }
}
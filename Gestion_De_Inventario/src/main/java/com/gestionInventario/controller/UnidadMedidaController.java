package com.gestionInventario.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.gestionInventario.dtos.response.PageDTO;
import jakarta.validation.Valid;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.dtos.response.UnidadMedidaDTO;
import com.gestionInventario.services.UnidadMedidaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unidades-medida")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UnidadMedidaController {

    private final UnidadMedidaService service;

    @GetMapping
    public ResponseEntity<PageDTO<UnidadMedidaDTO>> listarPaginado(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idUnidadMedida") String sortBy, 
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String buscar) {

        int pageIndex = Math.max(page - 1, 0);

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageIndex, size, sort);

        Page<UnidadMedidaDTO> pagina = service.listarConFiltros(buscar, pageable);

        PageDTO<UnidadMedidaDTO> response = new PageDTO<>(
                pagina.getContent(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.getNumber() + 1,
                pagina.getSize());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedidaDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UnidadMedidaDTO> crear(@Valid @RequestBody UnidadMedidaDTO dto) {
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedidaDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody UnidadMedidaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
}
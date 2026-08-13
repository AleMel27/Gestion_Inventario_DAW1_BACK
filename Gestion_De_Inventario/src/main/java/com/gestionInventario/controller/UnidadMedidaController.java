package com.gestionInventario.controller;

import java.util.List;

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
    public ResponseEntity<List<UnidadMedidaDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedidaDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UnidadMedidaDTO> crear(@RequestBody UnidadMedidaDTO dto) {
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedidaDTO> actualizar(@PathVariable Integer id, @RequestBody UnidadMedidaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
}
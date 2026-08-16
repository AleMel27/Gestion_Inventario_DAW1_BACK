package com.gestionInventario.controller;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.gestionInventario.dtos.response.PageDTO;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.model.Almacen;
import com.gestionInventario.services.AlmacenService;

@RestController
@RequestMapping("/api/almacenes")
@CrossOrigin(origins = "*")
public class AlmacenController {

    @Autowired
    private AlmacenService service;

 // ==========================================
    // MODIFICADO: Paginación agregada al GetMapping
    // ==========================================
    @GetMapping
    public ResponseEntity<PageDTO<Almacen>> listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "true") Boolean estado,
            @RequestParam(required = false) String nombre) {

        int size = 10;
        int pageIndex = Math.max(page - 1, 0);

        Page<Almacen> almacenes = service.listarConFiltros(
                estado,
                nombre,
                PageRequest.of(pageIndex, size));

        PageDTO<Almacen> response = new PageDTO<>(
                almacenes.getContent(),
                almacenes.getTotalElements(),
                almacenes.getTotalPages(),
                almacenes.getNumber() + 1,
                almacenes.getSize());

        return ResponseEntity.ok(response);
    }
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Almacen> obtenerPorId(@PathVariable Long id) {
        Almacen almacen = service.obtenerPorId(id);
        if (almacen == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(almacen);
    }

    @PostMapping
    public ResponseEntity<Almacen> registrar(@RequestBody Almacen almacen) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(almacen));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Almacen> actualizar(@PathVariable Long id, @RequestBody Almacen almacen) {
        Almacen actualizado = service.actualizar(id, almacen);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
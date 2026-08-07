package com.gestionInventario.controller;

import java.util.List;
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

    @GetMapping
    public ResponseEntity<List<Almacen>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

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
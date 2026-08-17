package com.gestionInventario.controller;

import com.gestionInventario.dtos.response.AlmacenDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.mapper.AlmacenMapper;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.services.AlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/almacenes")
@CrossOrigin(origins = "*")
public class AlmacenController {

    @Autowired
    private AlmacenService almacenService;
    
    @Autowired
    private AlmacenMapper mapper;
    
    // 1. Listar con paginación y filtros
    // GET /api/almacenes/paged?estado=true&nombre=central&page=0&size=10&sort=nombre,asc
    @GetMapping("/paginado")
    public ResponseEntity<PageDTO<AlmacenDTO>> listarPaginado(
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "idAlmacen") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
    	
    	int size = 10;
    	int pageIndex = Math.max(page - 1, 0);
       
    	Page<Almacen> almacen = almacenService.listarConFiltros(
    			estado, 
    			nombre, 
    			PageRequest.of(pageIndex, size));
    	List<AlmacenDTO> dtos = almacen.getContent()
    			.stream()
    			.map(mapper::convertirADto)
    			.collect(Collectors.toList());
    	PageDTO<AlmacenDTO> response = new PageDTO<>(
    			dtos,
    			almacen.getTotalElements(),
    			almacen.getTotalPages(),
    			almacen.getNumber() + 1,
    			almacen.getSize());
    	return ResponseEntity.ok(response);
    }

    // 2. Listar todos sin paginado
    // GET /api/almacenes
    @GetMapping
    public ResponseEntity<List<Almacen>> listarTodos() {
        return ResponseEntity.ok(almacenService.listarTodos());
    }

    // 3. Obtener por ID
    // GET /api/almacenes/1
    @GetMapping("/{id}")
    public ResponseEntity<Almacen> obtenerPorId(@PathVariable Long id) {
        Almacen almacen = almacenService.obtenerPorId(id);
        if (almacen != null) {
            return ResponseEntity.ok(almacen);
        }
        return ResponseEntity.notFound().build();
    }

    // 4. Registrar nuevo almacén
    // POST /api/almacenes
    @PostMapping
    public ResponseEntity<Almacen> registrar(@RequestBody Almacen almacen) {
        Almacen creado = almacenService.registrar(almacen);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // 5. Actualizar existente
    // PUT /api/almacenes/1
    @PutMapping("/{id}")
    public ResponseEntity<Almacen> actualizar(@PathVariable Long id, @RequestBody Almacen almacen) {
        Almacen actualizado = almacenService.actualizar(id, almacen);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // 6. Eliminar
    // DELETE /api/almacenes/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (almacenService.obtenerPorId(id) != null) {
            almacenService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
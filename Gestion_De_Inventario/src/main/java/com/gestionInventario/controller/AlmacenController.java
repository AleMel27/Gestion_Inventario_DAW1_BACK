package com.gestionInventario.controller;

import com.gestionInventario.dtos.request.AlmacenCreateDTO;
import com.gestionInventario.dtos.request.AlmacenUpdateDTO;
import com.gestionInventario.dtos.response.AlmacenDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.mapper.AlmacenMapper;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.services.AlmacenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    // GET /api/almacenes/paginado?estado=true&nombre=central&page=1&sortBy=nombre&direction=asc
    @GetMapping("/paginado")
    public ResponseEntity<PageDTO<AlmacenDTO>> listarPaginado(
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "idAlmacen") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
    	
        int size = 10;
        int pageIndex = Math.max(page - 1, 0);
        
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Page<Almacen> pageResult = almacenService.listarConFiltros(
                estado, 
                nombre, 
                PageRequest.of(pageIndex, size, sort));

        List<AlmacenDTO> dtos = pageResult.getContent()
                .stream()
                .map(mapper::convertirADto)
                .collect(Collectors.toList());

        PageDTO<AlmacenDTO> response = new PageDTO<>(
                dtos,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getNumber() + 1,
                pageResult.getSize());

        return ResponseEntity.ok(response);
    }

    // 2. Listar todos sin paginado (Devuelve DTOs)
    @GetMapping
    public ResponseEntity<List<AlmacenDTO>> listarTodos() {
        List<AlmacenDTO> dtos = almacenService.listarTodos()
                .stream()
                .map(mapper::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 3. Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<AlmacenDTO> obtenerPorId(@PathVariable Long id) {
        Almacen almacen = almacenService.obtenerPorId(id);
        if (almacen != null) {
            return ResponseEntity.ok(mapper.convertirADto(almacen));
        }
        return ResponseEntity.notFound().build();
    }

    // 4. Registrar nuevo almacén
    @PostMapping
    public ResponseEntity<AlmacenDTO> registrar(@RequestBody AlmacenCreateDTO dto) {
        Almacen entidad = mapper.convertirAEntidad(dto);
        Almacen creado = almacenService.registrar(entidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertirADto(creado));
    }

    // 5. Actualizar existente
    @PutMapping("/{id}")
    public ResponseEntity<AlmacenDTO> actualizar(@PathVariable Long id, @RequestBody AlmacenUpdateDTO dto) {
        Almacen almacenExistente = almacenService.obtenerPorId(id);
        if (almacenExistente == null) {
            return ResponseEntity.notFound().build();
        }

        mapper.actualizarEntidadDesdeDto(dto, almacenExistente);
        Almacen actualizado = almacenService.actualizar(id, almacenExistente);
        
        return ResponseEntity.ok(mapper.convertirADto(actualizado));
    }

    // 6. Eliminar (Eliminación Lógica: estado = false)
    // DELETE /api/almacenes/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = almacenService.eliminarLogico(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 7. Reactivar almacén (estado = true)
    // PATCH /api/almacenes/1/reactivar
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<AlmacenDTO> reactivar(@PathVariable Long id) {
        boolean reactivado = almacenService.reactivar(id);
        if (reactivado) {
            Almacen almacen = almacenService.obtenerPorId(id);
            return ResponseEntity.ok(mapper.convertirADto(almacen));
        }
        return ResponseEntity.notFound().build();
    }
}
package com.gestionInventario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestionInventario.dtos.request.ProveedorCreateDTO;
import com.gestionInventario.dtos.request.ProveedorUpdateDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.dtos.response.ProveedorDTO;
import com.gestionInventario.services.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    @Autowired
    private ProveedorService service;

    @GetMapping
    public ResponseEntity<PageDTO<ProveedorDTO>> listarPaginado(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idProveedor") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String buscar) {

        int pageIndex = Math.max(page - 1, 0);

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageIndex, size, sort);

        Page<ProveedorDTO> pagina = service.listarConFiltros(buscar, pageable);

        PageDTO<ProveedorDTO> response = new PageDTO<>(
                pagina.getContent(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.getNumber() + 1,
                pagina.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> obtenerPorId(@PathVariable Long id) {
        ProveedorDTO proveedor = service.obtenerPorId(id);
        if (proveedor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proveedor);
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> registrar(@Valid @RequestBody ProveedorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorUpdateDTO dto) {
        ProveedorDTO actualizado = service.actualizar(id, dto);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminarLogico(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) {
        boolean reactivado = service.reactivar(id);
        if (!reactivado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
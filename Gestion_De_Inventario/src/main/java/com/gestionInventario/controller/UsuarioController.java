package com.gestionInventario.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.gestionInventario.dtos.request.UsuarioCreateDTO;
import com.gestionInventario.dtos.request.UsuarioUpdateDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.dtos.response.UsuarioDTO;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionInventario.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

 // =========================================================================
    // GET PAGINADO CON FILTROS  HECHO
    // =========================================================================
    @GetMapping
    public ResponseEntity<PageDTO<UsuarioDTO>> listarPaginado(
    		@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idUsuario") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "") String buscar) {

        int pageIndex = Math.max(page - 1, 0);

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(pageIndex, size, sort);

        Page<UsuarioDTO> paginaDTO =
                service.listarConFiltros(buscar, pageable);

        PageDTO<UsuarioDTO> response = new PageDTO<>(
                paginaDTO.getContent(),
                paginaDTO.getTotalElements(),
                paginaDTO.getTotalPages(),
                paginaDTO.getNumber() + 1,
                paginaDTO.getSize()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioCreateDTO dto) { // @Valid agregado
		return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) { // @Valid agregado
		return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		boolean eliminado = service.eliminar(id);
		if (!eliminado) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id){
    	boolean reactivado = service.reactivar(id);
		if (!reactivado) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
    }

}

package com.gestionInventario.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestionInventario.dtos.request.CompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO;
import com.gestionInventario.dtos.response.CompraResumenDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.services.CompraService;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @GetMapping
    public ResponseEntity<PageDTO<CompraResumenDTO>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CompraResumenDTO> pagina = compraService.listarPaginado(pageable);
        PageDTO<CompraResumenDTO> response = new PageDTO<>(
                pagina.getContent(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.getNumber(),
                pagina.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CompraResDTO> registrar(@Valid @RequestBody CompraDTO dto) {
        CompraResDTO registrada = compraService.registrarCompra(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrada);
    }

    @PatchMapping("/{id}/recibir")
    public ResponseEntity<CompraResDTO> recibir(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.recibirCompra(id));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<CompraResDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.anularCompra(id));
    }
}

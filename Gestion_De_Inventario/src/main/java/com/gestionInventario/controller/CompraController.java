package com.gestionInventario.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestionInventario.dtos.response.CompraResDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.mapper.CompraMapper;
import com.gestionInventario.model.Compra;
import com.gestionInventario.model.DetalleCompra;
import com.gestionInventario.repository.IDetalleCompraRepository;
import com.gestionInventario.services.CompraService;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private CompraMapper compraMapper;

    @Autowired
    private IDetalleCompraRepository detalleCompraRepo;

    @GetMapping
    public ResponseEntity<PageDTO<CompraResDTO>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        // 1. Crear el objeto Pageable con ordenamiento
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 2. Obtener la página de Entidades desde el Service
        Page<Compra> pageCompras = compraService.listarPaginado(pageable);

        // 3. Mapear cada Compra a CompraResDTO trayendo sus detalles
        List<CompraResDTO> dtos = pageCompras.getContent().stream().map(compra -> {
            List<DetalleCompra> detalles = detalleCompraRepo.findByCompra_IdCompra(compra.getIdCompra());
            return compraMapper.convertirADto(compra, detalles);
        }).collect(Collectors.toList());

        // 4. Envolver en tu PageDTO
        PageDTO<CompraResDTO> response = new PageDTO<>(
                dtos,
                pageCompras.getTotalElements(),
                pageCompras.getTotalPages(),
                pageCompras.getNumber(),
                pageCompras.getSize()
        );

        return ResponseEntity.ok(response);
    }
}
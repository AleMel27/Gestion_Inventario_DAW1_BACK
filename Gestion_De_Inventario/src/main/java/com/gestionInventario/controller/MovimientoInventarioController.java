package com.gestionInventario.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestionInventario.dtos.request.MovimientoInventarioCreateDTO;
import com.gestionInventario.dtos.response.MovimientoInventarioResDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.services.MovimientoInventarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimientos-inventario")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    @GetMapping
    public ResponseEntity<PageDTO<MovimientoInventarioResDTO>> listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovimientoInventarioResDTO> pagina = movimientoInventarioService.listarPaginado(crearPageable(page, size));
        return ResponseEntity.ok(convertirPagina(pagina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoInventarioService.obtenerPorId(id));
    }

    @GetMapping("/producto/{idProducto}/almacen/{idAlmacen}")
    public ResponseEntity<PageDTO<MovimientoInventarioResDTO>> historialProductoAlmacen(
            @PathVariable Long idProducto,
            @PathVariable Long idAlmacen,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovimientoInventarioResDTO> pagina = movimientoInventarioService.listarHistorialProductoAlmacen(
                idProducto,
                idAlmacen,
                crearPageable(page, size));

        return ResponseEntity.ok(convertirPagina(pagina));
    }

    @PostMapping
    public ResponseEntity<MovimientoInventarioResDTO> registrarManual(
            @Valid @RequestBody MovimientoInventarioCreateDTO dto) {

        MovimientoInventarioResDTO movimiento = movimientoInventarioService.registrarManual(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }

    private Pageable crearPageable(int page, int size) {
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = Math.max(size, 1);
        Sort sort = Sort.by(
                Sort.Order.desc("fechaMovimiento"),
                Sort.Order.desc("idMovimiento"));
        return PageRequest.of(pageIndex, pageSize, sort);
    }

    private PageDTO<MovimientoInventarioResDTO> convertirPagina(Page<MovimientoInventarioResDTO> pagina) {
        return new PageDTO<>(
                pagina.getContent(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.getNumber() + 1,
                pagina.getSize());
    }
}

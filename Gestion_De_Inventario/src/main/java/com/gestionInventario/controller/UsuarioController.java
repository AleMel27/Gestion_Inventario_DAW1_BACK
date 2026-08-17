package com.gestionInventario.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.gestionInventario.dtos.request.UsuarioCreateDTO;
import com.gestionInventario.dtos.request.UsuarioUpdateDTO;
import com.gestionInventario.dtos.response.PageDTO;
import com.gestionInventario.dtos.response.UsuarioDTO;
import com.gestionInventario.mapper.UsuarioMapper;

import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.gestionInventario.model.Usuario;
import com.gestionInventario.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    
    @Autowired
    private UsuarioMapper mapper;

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

        Page<Usuario> pagina =
                service.listarConFiltros(buscar, pageable);

        Page<UsuarioDTO> paginaDTO =
                pagina.map(mapper::convertirADto);

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
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        Usuario usuario = service.obtenerPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioCreateDTO dto) { // @Valid agregado
		Usuario usuario = mapper.covertirDtoCreate(dto);
		Usuario registrado = service.registrar(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convertirADto(registrado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) { // @Valid agregado
		Usuario usuario = mapper.convertirDtoUpdate(dto);
		Usuario actualizado = service.actualizar(id, usuario);
		if (actualizado == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(mapper.convertirADto(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		boolean eliminado = service.eliminar(id);
		if (!eliminado) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
    }

    // ==========================================
    // RUTA PARA POSTMAN / ANGULAR (LOGIN)
    // Descomentar cuando habilites el método login en UsuarioService
    // ==========================================
    /*
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        try {
            Usuario logueado = service.login(usuario);
            return ResponseEntity.ok(logueado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    */
}
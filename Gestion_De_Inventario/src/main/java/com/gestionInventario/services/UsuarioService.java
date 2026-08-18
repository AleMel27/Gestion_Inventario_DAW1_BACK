package com.gestionInventario.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gestionInventario.dtos.request.UsuarioCreateDTO;
import com.gestionInventario.dtos.request.UsuarioUpdateDTO;
import com.gestionInventario.dtos.response.UsuarioDTO;
import com.gestionInventario.exception.ResourceNotFoundException;
import com.gestionInventario.mapper.UsuarioMapper;
import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IRolRepository;
import com.gestionInventario.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final IUsuarioRepository repo;
    private final IRolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    public Page<UsuarioDTO> listarConFiltros(String buscar, Pageable pageable) {
        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(buscar)) {
            String filtro = "%" + buscar.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombres")), filtro),
                    cb.like(cb.lower(root.get("apellidos")), filtro),
                    cb.like(cb.lower(root.get("correo")), filtro)));
        }

        return repo.findAll(spec, pageable).map(mapper::convertirADto);
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = obtenerUsuarioExistente(id);
        return mapper.convertirADto(usuario);
    }

    public UsuarioDTO registrar(UsuarioCreateDTO dto) {
        Rol rol = obtenerRolExistente(dto.getIdRol());

        Usuario usuario = mapper.convertirDtoCreate(dto);
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);
        usuario.setEstado(true);

        Usuario registrado = repo.save(usuario);
        return mapper.convertirADto(registrado);
    }

    public UsuarioDTO actualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuarioExistente = obtenerUsuarioExistente(id);
        Rol rol = obtenerRolExistente(dto.getIdRol());

        mapper.actualizarEntidad(usuarioExistente, dto);
        usuarioExistente.setRol(rol);

        if (StringUtils.hasText(dto.getPassword())) {
            usuarioExistente.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario actualizado = repo.save(usuarioExistente);
        return mapper.convertirADto(actualizado);
    }

    public boolean eliminar(Long id) {
        Usuario usuarioExistente = repo.findById(id).orElse(null);

        if (usuarioExistente == null) {
            return false;
        }

        usuarioExistente.setEstado(false);
        repo.save(usuarioExistente);
        return true;
    }

    public boolean reactivar(Long id) {
        Usuario usuarioExistente = repo.findById(id).orElse(null);

        if (usuarioExistente == null) {
            return false;
        }

        usuarioExistente.setEstado(true);
        repo.save(usuarioExistente);
        return true;
    }

    private Usuario obtenerUsuarioExistente(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
    }

    private Rol obtenerRolExistente(Short idRol) {
        return rolRepo.findById(idRol)
                .orElseThrow(() -> new ResourceNotFoundException("El rol no existe"));
    }
}

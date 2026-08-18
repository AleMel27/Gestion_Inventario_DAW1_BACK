package com.gestionInventario.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    private static final String AUTHORITY_ADMINISTRADOR = "ROLE_ADMINISTRADOR";

    private final IUsuarioRepository repo;
    private final IRolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    @Value("${security.bootstrap-admin-enabled:false}")
    private boolean bootstrapAdminEnabled;

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

    @Transactional
    public UsuarioDTO registrar(UsuarioCreateDTO dto) {
        if (esRegistroAnonimo()) {
            return registrarBootstrap(dto);
        }

        if (!tieneRolAdministrador()) {
            throw new AccessDeniedException("No tiene permisos para registrar usuarios");
        }

        Rol rol = obtenerRolExistente(dto.getIdRol());
        return guardarUsuario(dto, rol);
    }

    private UsuarioDTO registrarBootstrap(UsuarioCreateDTO dto) {
        if (!bootstrapAdminEnabled) {
            throw new AccessDeniedException("Bootstrap de administrador deshabilitado");
        }

        Rol rolSolicitado = obtenerRolExistente(dto.getIdRol());
        if (!ROL_ADMINISTRADOR.equals(rolSolicitado.getNombre())) {
            throw new AccessDeniedException("El bootstrap solo permite registrar el primer administrador");
        }

        Rol rolAdministrador = rolRepo.findByNombre(ROL_ADMINISTRADOR)
                .orElseThrow(() -> new ResourceNotFoundException("El rol ADMINISTRADOR no existe"));

        if (!rolAdministrador.getIdRol().equals(rolSolicitado.getIdRol())) {
            throw new AccessDeniedException("El rol solicitado no corresponde al administrador");
        }

        if (repo.existsByEstadoTrueAndRolNombre(ROL_ADMINISTRADOR)) {
            throw new AccessDeniedException("Ya existe un administrador activo");
        }

        return guardarUsuario(dto, rolAdministrador);
    }

    private UsuarioDTO guardarUsuario(UsuarioCreateDTO dto, Rol rol) {
        Usuario usuario = mapper.convertirDtoCreate(dto);
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);
        usuario.setEstado(true);

        Usuario registrado = repo.save(usuario);
        return mapper.convertirADto(registrado);
    }

    private boolean esRegistroAnonimo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null
                || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated();
    }

    private boolean tieneRolAdministrador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> AUTHORITY_ADMINISTRADOR.equals(authority.getAuthority()));
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
        Usuario usuarioExistente = obtenerUsuarioExistente(id);

        usuarioExistente.setEstado(false);
        repo.save(usuarioExistente);
        return true;
    }

    public boolean reactivar(Long id) {
        Usuario usuarioExistente = obtenerUsuarioExistente(id);

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

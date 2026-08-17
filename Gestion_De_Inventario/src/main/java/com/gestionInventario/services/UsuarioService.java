package com.gestionInventario.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils; // AGREGADO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// ==========================================
// IMPORTS PARA SPRING SECURITY
// Descomentar cuando implementen autenticación
// ==========================================
// import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IRolRepository;
import com.gestionInventario.repository.IUsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private IUsuarioRepository repo;

	@Autowired
	private IRolRepository rolRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// =========================================================================
	// MÉTODO AGREGADO: Paginado dinámico con filtro por nombres, apellidos o correo
	// =========================================================================
	public Page<Usuario> listarConFiltros(String buscar, Pageable pageable) {
		Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

		if (StringUtils.hasText(buscar)) {
			String filtro = "%" + buscar.trim().toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.or(cb.like(cb.lower(root.get("nombres")), filtro),
					cb.like(cb.lower(root.get("apellidos")), filtro), cb.like(cb.lower(root.get("correo")), filtro)));
		}

		return repo.findAll(spec, pageable);
	}
	// =========================================================================

	public List<Usuario> listarTodos() {
		return repo.findAll();
	}

	public Usuario obtenerPorId(Long id) {
		return repo.findById(id).orElse(null);
	}

	public Usuario registrar(Usuario usuario) {
		String passwordCodificado = passwordEncoder.encode(usuario.getPasswordHash());
		usuario.setPasswordHash(passwordCodificado);

		asignarRolExistente(usuario);
		return repo.save(usuario);
	}

	public Usuario actualizar(Long id, Usuario usuario) {
		Optional<Usuario> usuarioExistenteOpt = repo.findById(id);

		if (usuarioExistenteOpt.isPresent()) {
			Usuario usuarioExistente = usuarioExistenteOpt.get();

			// 1. Mapeamos los campos modificables normales
			usuarioExistente.setNombres(usuario.getNombres());
			usuarioExistente.setApellidos(usuario.getApellidos());
			usuarioExistente.setCorreo(usuario.getCorreo());
			usuarioExistente.setRol(usuario.getRol());
			usuarioExistente.setEstado(usuario.getEstado());

			if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().trim().isEmpty()) {

				if (!usuario.getPasswordHash().equals(usuarioExistente.getPasswordHash())) {
					String passwordCodificado = passwordEncoder.encode(usuario.getPasswordHash());
					usuarioExistente.setPasswordHash(passwordCodificado);
				}

				usuarioExistente.setPasswordHash(usuario.getPasswordHash());
			}

			asignarRolExistente(usuarioExistente);
			return repo.save(usuarioExistente);
		}

		return null;
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

	private void asignarRolExistente(Usuario usuario) {
		if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
			throw new RuntimeException("El rol es obligatorio");
		}

		usuario.setRol(obtenerRolExistente(usuario.getRol().getIdRol()));
	}

	public Usuario login(Usuario usuario) {
		Usuario encontrado = repo.findByCorreo(usuario.getCorreo())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		if (!passwordEncoder.matches(usuario.getPasswordHash(), encontrado.getPasswordHash())) {
			throw new RuntimeException("Contraseña incorrecta");
		}
		return encontrado;
	}

	private Rol obtenerRolExistente(Short idRol) {

		return rolRepo.findById(idRol).orElseThrow(() -> new RuntimeException("El rol no existe"));
	}

}

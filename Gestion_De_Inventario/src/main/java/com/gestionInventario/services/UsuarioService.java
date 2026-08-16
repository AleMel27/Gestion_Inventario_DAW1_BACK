package com.gestionInventario.services;

import java.util.List;
import java.util.Optional;


import org.springframework.data.domain.Page; // AGREGADO
import org.springframework.data.domain.Pageable; // AGREGADO
import org.springframework.data.jpa.domain.Specification; // AGREGADO
import org.springframework.transaction.annotation.Transactional; // AGREGADO
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

    // ==========================================
    // SPRING SECURITY
    // Descomentar cuando creen PasswordEncoderConfig
    // ==========================================
    /*
    @Autowired
    private PasswordEncoder passwordEncoder;
    */
    
    
    
    
    
 // =========================================================================
    // MÉTODO AGREGADO: Paginado dinámico con filtro por nombres, apellidos o correo
    // =========================================================================
    @Transactional(readOnly = true)
    public Page<Usuario> listarConFiltros(String buscar, Pageable pageable) {
        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(buscar)) {
            String filtro = "%" + buscar.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("nombres")), filtro),
                cb.like(cb.lower(root.get("apellidos")), filtro),
                cb.like(cb.lower(root.get("correo")), filtro)
            ));
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
        // ==========================================
        // SPRING SECURITY
        // Encriptar la contraseña antes de guardar
        // ==========================================
        /*
        String passwordCodificado = passwordEncoder.encode(usuario.getPasswordHash());
        usuario.setPasswordHash(passwordCodificado);
        */
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
            
            // 2. Lógica inteligente para la contraseña
            // Si el cliente envía una contraseña nueva (no está vacía/nula) y es diferente a la actual
            if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().trim().isEmpty()) {
                
                // ==========================================
                // MODO CON SPRING SECURITY (Descomentar al activar seguridad)
                // ==========================================
                /*
                // Solo encriptamos si no coincide con el hash almacenado (es decir, viene en texto plano desde el formulario)
                if (!usuario.getPasswordHash().equals(usuarioExistente.getPasswordHash())) {
                    String passwordCodificado = passwordEncoder.encode(usuario.getPasswordHash());
                    usuarioExistente.setPasswordHash(passwordCodificado);
                }
                */
                
                // ==========================================
                // MODO SIN SECURITY (Temporal)
                // ==========================================
                // Quitar esta línea de abajo cuando actives Spring Security
                usuarioExistente.setPasswordHash(usuario.getPasswordHash());
            }
            // NOTA: Si usuario.getPasswordHash() viene vacío o nulo, NO se altera el passwordHash existente.

            // 3. Guardamos la entidad persistida/actualizada
            asignarRolExistente(usuarioExistente);
            return repo.save(usuarioExistente);
        }
        
        return null; // O puedes lanzar una excepción personalizada si prefieres
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    private void asignarRolExistente(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
            throw new RuntimeException("El rol es obligatorio");
        }

        Rol rol = rolRepo.findById(usuario.getRol().getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
    }
    
    /*
    public Usuario login(Usuario usuario) {
        Usuario encontrado = repo.findByCorreo(usuario.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(usuario.getPasswordHash(), encontrado.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        return encontrado;
    }
    */
}

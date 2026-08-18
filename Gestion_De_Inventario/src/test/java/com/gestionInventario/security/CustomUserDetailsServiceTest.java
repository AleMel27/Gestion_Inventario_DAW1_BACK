package com.gestionInventario.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new CustomUserDetailsService(usuarioRepository);
    }

    @Test
    void usuarioExistenteActivoConstruyeUserDetailsConAuthority() {
        Usuario usuario = usuario("ADMINISTRADOR", true);
        when(usuarioRepository.findByCorreo("admin@licores.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("admin@licores.com");

        assertEquals("admin@licores.com", userDetails.getUsername());
        assertEquals("$2a$hash", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRADOR")));
    }

    @Test
    void usuarioInexistenteLanzaUsernameNotFoundException() {
        when(usuarioRepository.findByCorreo("missing@licores.com")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@licores.com"));
    }

    @Test
    void usuarioInactivoQuedaDisabled() {
        Usuario usuario = usuario("ADMINISTRADOR", false);
        when(usuarioRepository.findByCorreo("admin@licores.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("admin@licores.com");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void rolAlmaceneroConstruyeRoleAlmacenero() {
        Usuario usuario = usuario("ALMACENERO", true);
        when(usuarioRepository.findByCorreo("admin@licores.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("admin@licores.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ALMACENERO")));
    }

    @Test
    void rolConPrefijoNoDuplicaRole() {
        Usuario usuario = usuario("ROLE_ADMINISTRADOR", true);
        when(usuarioRepository.findByCorreo("admin@licores.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("admin@licores.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRADOR")));
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ROLE_ADMINISTRADOR")));
    }

    private Usuario usuario(String rolNombre, boolean estado) {
        Rol rol = new Rol();
        rol.setIdRol((short) 1);
        rol.setNombre(rolNombre);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCorreo("admin@licores.com");
        usuario.setPasswordHash("$2a$hash");
        usuario.setEstado(estado);
        usuario.setRol(rol);
        return usuario;
    }
}

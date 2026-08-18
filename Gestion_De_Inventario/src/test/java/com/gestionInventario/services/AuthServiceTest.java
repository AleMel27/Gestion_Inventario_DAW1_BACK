package com.gestionInventario.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestionInventario.dtos.request.LoginRequestDTO;
import com.gestionInventario.dtos.response.AuthResponseDTO;
import com.gestionInventario.mapper.AuthMapper;
import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                usuarioRepository,
                passwordEncoder,
                jwtService,
                new AuthMapper());
    }

    @Test
    void loginCorrectoDevuelveTokenYUsuario() {
        Usuario usuario = usuarioActivo();
        LoginRequestDTO request = loginRequest("admin@gmail.com", "123456");

        when(usuarioRepository.findByCorreo("admin@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hash-bcrypt")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("jwt-token");
        when(jwtService.obtenerExpiracion("jwt-token")).thenReturn(Instant.parse("2026-08-18T01:49:05Z"));

        AuthResponseDTO response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(Instant.parse("2026-08-18T01:49:05Z"), response.getExpiration());
        assertEquals(1L, response.getUsuario().getIdUsuario());
        assertEquals("admin@gmail.com", response.getUsuario().getCorreo());
        assertEquals("ADMINISTRADOR", response.getUsuario().getRol().getNombre());
        assertEquals((short) 1, response.getUsuario().getRol().getIdRol());
        assertEquals(true, response.getUsuario().getEstado());
    }

    @Test
    void passwordIncorrectaDevuelveCredencialesInvalidas() {
        Usuario usuario = usuarioActivo();
        LoginRequestDTO request = loginRequest("admin@gmail.com", "bad");

        when(usuarioRepository.findByCorreo("admin@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("bad", "hash-bcrypt")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));

        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(jwtService, never()).generarToken(usuario);
    }

    @Test
    void correoInexistenteDevuelveCredencialesInvalidas() {
        LoginRequestDTO request = loginRequest("noexiste@gmail.com", "123456");
        when(usuarioRepository.findByCorreo("noexiste@gmail.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));

        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(passwordEncoder, never()).matches(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString());
    }

    @Test
    void usuarioInactivoRechazaLogin() {
        Usuario usuario = usuarioActivo();
        usuario.setEstado(false);
        LoginRequestDTO request = loginRequest("admin@gmail.com", "123456");

        when(usuarioRepository.findByCorreo("admin@gmail.com")).thenReturn(Optional.of(usuario));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));

        assertEquals("Usuario inactivo", ex.getMessage());
        verify(passwordEncoder, never()).matches(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString());
    }

    private LoginRequestDTO loginRequest(String correo, String password) {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setCorreo(correo);
        request.setPassword(password);
        return request;
    }

    private Usuario usuarioActivo() {
        Rol rol = new Rol();
        rol.setIdRol((short) 1);
        rol.setNombre("ADMINISTRADOR");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombres("Leonardo");
        usuario.setApellidos("Perez");
        usuario.setCorreo("admin@gmail.com");
        usuario.setPasswordHash("hash-bcrypt");
        usuario.setEstado(true);
        usuario.setRol(rol);
        return usuario;
    }
}

package com.gestionInventario.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        Rol rol = new Rol();
        rol.setIdRol((short) 1);
        rol.setNombre("ADMINISTRADOR");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCorreo("admin@gmail.com");
        usuario.setRol(rol);
    }

    @Test
    void generaTokenYExtraeCorreoYClaims() {
        String token = jwtService.generarToken(usuario);

        Claims claims = jwtService.extraerClaims(token);

        assertEquals("admin@gmail.com", jwtService.extraerCorreo(token));
        assertEquals(1, claims.get("idUsuario", Integer.class));
        assertEquals("ADMINISTRADOR", claims.get("rol", String.class));
        assertEquals(claims.getExpiration().toInstant(), jwtService.obtenerExpiracion(token));
    }

    @Test
    void validaTokenValido() {
        String token = jwtService.generarToken(usuario);

        assertTrue(jwtService.validarToken(token, usuario));
    }

    @Test
    void rechazaTokenConOtroUsuario() {
        String token = jwtService.generarToken(usuario);
        Usuario otroUsuario = new Usuario();
        otroUsuario.setCorreo("otro@gmail.com");

        assertFalse(jwtService.validarToken(token, otroUsuario));
    }

    @Test
    void rechazaTokenExpirado() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String token = jwtService.generarToken(usuario);

        assertTrue(jwtService.esTokenExpirado(token));
        assertFalse(jwtService.validarToken(token, usuario));
    }
}

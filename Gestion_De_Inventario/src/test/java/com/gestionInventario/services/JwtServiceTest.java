package com.gestionInventario.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final String OTRO_SECRET = "abcdefghijklmnopqrstuvwx12345678";
    private static final String ISSUER = "gestion-inventario-api";
    private static final String AUDIENCE = "gestion-inventario-client";

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "issuer", ISSUER);
        ReflectionTestUtils.setField(jwtService, "audience", AUDIENCE);

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
        assertEquals(ISSUER, claims.getIssuer());
        assertTrue(claims.getAudience().contains(AUDIENCE));
        assertEquals(claims.getExpiration().toInstant(), jwtService.obtenerExpiracion(token));
    }

    @Test
    void validaTokenValido() {
        String token = jwtService.generarToken(usuario);

        assertTrue(jwtService.validarToken(token, usuario));
        assertTrue(jwtService.validarToken(token, userDetails("admin@gmail.com", true)));
    }

    @Test
    void rechazaTokenConOtroUsuario() {
        String token = jwtService.generarToken(usuario);
        Usuario otroUsuario = new Usuario();
        otroUsuario.setCorreo("otro@gmail.com");

        assertFalse(jwtService.validarToken(token, otroUsuario));
        assertFalse(jwtService.validarToken(token, userDetails("otro@gmail.com", true)));
    }

    @Test
    void rechazaTokenExpirado() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String token = jwtService.generarToken(usuario);

        assertTrue(jwtService.esTokenExpirado(token));
        assertFalse(jwtService.validarToken(token, usuario));
    }

    @Test
    void rechazaTokenConIssuerIncorrecto() {
        JwtService emisorIncorrecto = jwtServiceCon(SECRET, 3600000L, "otro-issuer", AUDIENCE);
        String token = emisorIncorrecto.generarToken(usuario);

        assertFalse(jwtService.validarToken(token, userDetails("admin@gmail.com", true)));
    }

    @Test
    void rechazaTokenConAudienceIncorrecta() {
        JwtService audienciaIncorrecta = jwtServiceCon(SECRET, 3600000L, ISSUER, "otra-audience");
        String token = audienciaIncorrecta.generarToken(usuario);

        assertFalse(jwtService.validarToken(token, userDetails("admin@gmail.com", true)));
    }

    @Test
    void rechazaTokenConFirmaIncorrecta() {
        JwtService otroFirmante = jwtServiceCon(OTRO_SECRET, 3600000L, ISSUER, AUDIENCE);
        String token = otroFirmante.generarToken(usuario);

        assertFalse(jwtService.validarToken(token, userDetails("admin@gmail.com", true)));
    }

    @Test
    void rechazaUserDetailsDeshabilitado() {
        String token = jwtService.generarToken(usuario);

        assertFalse(jwtService.validarToken(token, userDetails("admin@gmail.com", false)));
    }

    private JwtService jwtServiceCon(String secret, long expiration, String issuer, String audience) {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secret", secret);
        ReflectionTestUtils.setField(service, "expiration", expiration);
        ReflectionTestUtils.setField(service, "issuer", issuer);
        ReflectionTestUtils.setField(service, "audience", audience);
        return service;
    }

    private UserDetails userDetails(String username, boolean enabled) {
        User.UserBuilder builder = User.withUsername(username)
                .password("hash")
                .authorities("ROLE_ADMINISTRADOR");

        if (!enabled) {
            builder.disabled(true);
        }

        return builder.build();
    }
}

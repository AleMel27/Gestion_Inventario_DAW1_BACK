package com.gestionInventario.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.gestionInventario.services.JwtService;

import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sinAuthorizationHeaderContinuaSinAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extraerCorreo(org.mockito.Mockito.anyString());
    }

    @Test
    void headerSinBearerContinuaSinAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        request.addHeader("Authorization", "Basic abc");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extraerCorreo(org.mockito.Mockito.anyString());
    }

    @Test
    void tokenValidoEstableceSecurityContext() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        UserDetails userDetails = userDetails("admin@licores.com", true);

        when(jwtService.extraerCorreo("token-valido")).thenReturn("admin@licores.com");
        when(userDetailsService.loadUserByUsername("admin@licores.com")).thenReturn(userDetails);
        when(jwtService.validarToken("token-valido", userDetails)).thenReturn(true);

        filter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userDetails, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRADOR")));
    }

    @Test
    void tokenInvalidoNoEstableceAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        request.addHeader("Authorization", "Bearer token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.extraerCorreo("token-invalido")).thenThrow(new IllegalArgumentException("Token inválido"));

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void usuarioInactivoNoQuedaAutenticado() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        UserDetails userDetails = userDetails("admin@licores.com", false);

        when(jwtService.extraerCorreo("token-valido")).thenReturn("admin@licores.com");
        when(userDetailsService.loadUserByUsername("admin@licores.com")).thenReturn(userDetails);
        when(jwtService.validarToken("token-valido", userDetails)).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
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

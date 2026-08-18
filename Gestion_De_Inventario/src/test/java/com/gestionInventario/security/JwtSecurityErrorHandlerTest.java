package com.gestionInventario.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

class JwtSecurityErrorHandlerTest {

    @Test
    void entryPointDevuelveJsonUnauthorized() throws Exception {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new AuthenticationException("No autenticado") {
        });

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"status\":401"));
        assertTrue(response.getContentAsString().contains("\"error\":\"Unauthorized\""));
        assertTrue(response.getContentAsString().contains("\"message\":\"No autenticado\""));
    }

    @Test
    void accessDeniedHandlerDevuelveJsonForbidden() throws Exception {
        JwtAccessDeniedHandler handler = new JwtAccessDeniedHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Sin permisos"));

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"status\":403"));
        assertTrue(response.getContentAsString().contains("\"error\":\"Forbidden\""));
        assertTrue(response.getContentAsString()
                .contains("\"message\":\"No tiene permisos para realizar esta operación\""));
    }
}

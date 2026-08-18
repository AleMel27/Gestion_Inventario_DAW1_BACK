package com.gestionInventario.services;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestionInventario.dtos.request.LoginRequestDTO;
import com.gestionInventario.dtos.response.AuthResponseDTO;
import com.gestionInventario.mapper.AuthMapper;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    public AuthResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException(CREDENCIALES_INVALIDAS));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException(CREDENCIALES_INVALIDAS);
        }

        String token = jwtService.generarToken(usuario);
        Instant expiration = jwtService.obtenerExpiracion(token);
        return authMapper.convertirAAuthResponse(usuario, token, expiration);
    }
}

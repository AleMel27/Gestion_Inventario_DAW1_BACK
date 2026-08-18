package com.gestionInventario.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUsuario", usuario.getIdUsuario());
        claims.put("rol", usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return generarToken(claims, usuario.getCorreo());
    }

    public String generarToken(Map<String, Object> claims, String subject) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(obtenerClaveFirma(), Jwts.SIG.HS256)
                .compact();
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extraerClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClaveFirma())
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esTokenExpirado(String token) {
        try {
            return extraerClaim(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public boolean validarToken(String token, Usuario usuario) {
        try {
            String correo = extraerCorreo(token);
            return correo != null
                    && !correo.isBlank()
                    && correo.equals(usuario.getCorreo())
                    && !esTokenExpirado(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        try {
            String correo = extraerCorreo(token);
            return correo != null
                    && !correo.isBlank()
                    && correo.equals(userDetails.getUsername())
                    && userDetails.isEnabled()
                    && !esTokenExpirado(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Instant obtenerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration).toInstant();
    }

    private SecretKey obtenerClaveFirma() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("La propiedad jwt.secret no está configurada");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("La propiedad jwt.secret debe tener al menos 32 bytes para HS256");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}

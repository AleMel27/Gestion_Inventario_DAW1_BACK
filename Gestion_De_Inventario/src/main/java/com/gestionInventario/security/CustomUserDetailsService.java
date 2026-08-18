package com.gestionInventario.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPasswordHash())
                .disabled(!Boolean.TRUE.equals(usuario.getEstado()))
                .authorities(convertirAuthorities(usuario))
                .build();
    }

    private List<GrantedAuthority> convertirAuthorities(Usuario usuario) {
        Rol rol = usuario.getRol();
        if (rol == null || rol.getNombre() == null || rol.getNombre().isBlank()) {
            throw new UsernameNotFoundException("El usuario no tiene un rol válido");
        }

        String nombreRol = rol.getNombre().trim();
        String authority = nombreRol.startsWith("ROLE_") ? nombreRol : "ROLE_" + nombreRol;
        return List.of(new SimpleGrantedAuthority(authority));
    }
}

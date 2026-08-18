package com.gestionInventario.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestionInventario.dtos.request.UsuarioCreateDTO;
import com.gestionInventario.dtos.request.UsuarioUpdateDTO;
import com.gestionInventario.dtos.response.UsuarioDTO;
import com.gestionInventario.exception.ResourceNotFoundException;
import com.gestionInventario.mapper.UsuarioMapper;
import com.gestionInventario.model.Rol;
import com.gestionInventario.model.Usuario;
import com.gestionInventario.repository.IRolRepository;
import com.gestionInventario.repository.IUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IRolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(
                usuarioRepository,
                rolRepository,
                passwordEncoder,
                new UsuarioMapper());
    }

    @Test
    void registrarCorrectamenteAsignaRolCodificaPasswordYDevuelveDto() {
        UsuarioCreateDTO dto = usuarioCreateDTO();
        Rol rol = rol("ADMINISTRADOR");

        when(rolRepository.findById((short) 1)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("MiPassword123!")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setIdUsuario(4L);
            return usuario;
        });

        UsuarioDTO response = service.registrar(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertEquals("Leonardo", guardado.getNombres());
        assertEquals("leonardo@gmail.com", guardado.getCorreo());
        assertEquals(rol, guardado.getRol());
        assertEquals("$2a$hash", guardado.getPasswordHash());
        assertNotEquals("MiPassword123!", guardado.getPasswordHash());
        assertEquals(true, guardado.getEstado());

        assertEquals(4L, response.getIdUsuario());
        assertEquals("Leonardo", response.getNombres());
        assertEquals("leonardo@gmail.com", response.getCorreo());
        assertEquals((short) 1, response.getRol().getIdRol());
        assertEquals("ADMINISTRADOR", response.getRol().getNombre());
    }

    @Test
    void registrarConRolInexistenteNoGuardaUsuario() {
        UsuarioCreateDTO dto = usuarioCreateDTO();
        dto.setIdRol((short) 99);
        when(rolRepository.findById((short) 99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.registrar(dto));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateSinPasswordConservaPasswordHashAnterior() {
        Usuario existente = usuarioExistente();
        UsuarioUpdateDTO dto = usuarioUpdateDTO();
        dto.setPassword(null);
        Rol rol = rol("ALMACENERO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById((short) 1)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO response = service.actualizar(1L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertEquals("hash-anterior", guardado.getPasswordHash());
        verify(passwordEncoder, never()).encode(any());
        assertEquals("Nuevo", response.getNombres());
        assertEquals("ALMACENERO", response.getRol().getNombre());
    }

    @Test
    void updateConPasswordCodificaNuevaPassword() {
        Usuario existente = usuarioExistente();
        UsuarioUpdateDTO dto = usuarioUpdateDTO();
        dto.setPassword("PasswordNueva123!");
        Rol rol = rol("ALMACENERO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById((short) 1)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("PasswordNueva123!")).thenReturn("$2a$nuevohash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.actualizar(1L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertEquals("$2a$nuevohash", captor.getValue().getPasswordHash());
        assertNotEquals("PasswordNueva123!", captor.getValue().getPasswordHash());
    }

    @Test
    void obtenerPorIdDevuelveUsuarioDto() {
        Usuario existente = usuarioExistente();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

        UsuarioDTO response = service.obtenerPorId(1L);

        assertEquals(1L, response.getIdUsuario());
        assertEquals("leonardo@gmail.com", response.getCorreo());
        assertEquals((short) 1, response.getRol().getIdRol());
        assertEquals("ADMINISTRADOR", response.getRol().getNombre());
    }

    private UsuarioCreateDTO usuarioCreateDTO() {
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNombres("Leonardo");
        dto.setApellidos("Perez");
        dto.setCorreo("leonardo@gmail.com");
        dto.setPassword("MiPassword123!");
        dto.setIdRol((short) 1);
        return dto;
    }

    private UsuarioUpdateDTO usuarioUpdateDTO() {
        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setNombres("Nuevo");
        dto.setApellidos("Usuario");
        dto.setCorreo("nuevo@gmail.com");
        dto.setIdRol((short) 1);
        return dto;
    }

    private Usuario usuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombres("Leonardo");
        usuario.setApellidos("Perez");
        usuario.setCorreo("leonardo@gmail.com");
        usuario.setPasswordHash("hash-anterior");
        usuario.setEstado(true);
        usuario.setRol(rol("ADMINISTRADOR"));
        return usuario;
    }

    private Rol rol(String nombre) {
        Rol rol = new Rol();
        rol.setIdRol((short) 1);
        rol.setNombre(nombre);
        rol.setEstado(true);
        return rol;
    }
}

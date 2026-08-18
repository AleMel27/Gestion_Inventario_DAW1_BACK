package com.gestionInventario.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // AGREGADO

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gestionInventario.model.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByEstadoTrueAndRolNombre(String nombreRol);
}

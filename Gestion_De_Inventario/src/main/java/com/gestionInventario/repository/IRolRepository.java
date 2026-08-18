package com.gestionInventario.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Rol;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Short> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Rol> findByNombre(String nombre);
}

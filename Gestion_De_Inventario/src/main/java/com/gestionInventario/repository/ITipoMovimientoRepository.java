package com.gestionInventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.TipoMovimiento;

@Repository
public interface ITipoMovimientoRepository extends JpaRepository<TipoMovimiento, Integer> {

    Optional<TipoMovimiento> findByNombre(String codigo);
}

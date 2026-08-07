package com.gestionInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.MovimientoInventario;

@Repository
public interface IMovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

}

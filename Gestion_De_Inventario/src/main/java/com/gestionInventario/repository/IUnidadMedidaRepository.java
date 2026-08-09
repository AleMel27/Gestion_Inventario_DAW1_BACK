package com.gestionInventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.UnidadMedida;

@Repository
public interface IUnidadMedidaRepository extends JpaRepository<UnidadMedida, Integer> {

    Optional<UnidadMedida> findByCodigo(String codigo);
}

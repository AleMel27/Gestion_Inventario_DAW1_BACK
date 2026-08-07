package com.gestionInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Inventario;

@Repository
public interface IInventarioRepository extends JpaRepository<Inventario, Long> {

}

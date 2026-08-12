package com.gestionInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Categoria;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {
}

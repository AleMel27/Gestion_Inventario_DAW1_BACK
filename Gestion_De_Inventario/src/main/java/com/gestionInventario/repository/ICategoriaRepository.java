package com.gestionInventario.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Categoria;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {

    Page<Categoria> findByEstado(Boolean estado, Pageable pageable);
}

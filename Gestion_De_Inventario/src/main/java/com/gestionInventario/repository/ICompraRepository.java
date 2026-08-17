package com.gestionInventario.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Compra;

@Repository
public interface ICompraRepository extends JpaRepository<Compra, Long>, JpaSpecificationExecutor<Compra> {

    @Override
    @EntityGraph(attributePaths = {"proveedor", "usuario", "almacen", "tipoComprobante"})
    Page<Compra> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"proveedor", "usuario", "almacen", "tipoComprobante"})
    Optional<Compra> findById(Long id);
}

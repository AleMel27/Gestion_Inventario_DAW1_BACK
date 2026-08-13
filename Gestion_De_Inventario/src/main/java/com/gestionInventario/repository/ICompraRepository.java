package com.gestionInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Compra;

@Repository
public interface ICompraRepository extends JpaRepository<Compra, Long>, JpaSpecificationExecutor<Compra> {
}
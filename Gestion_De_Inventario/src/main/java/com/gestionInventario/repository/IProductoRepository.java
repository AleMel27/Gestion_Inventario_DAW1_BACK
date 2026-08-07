package com.gestionInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Producto;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Long> {

}

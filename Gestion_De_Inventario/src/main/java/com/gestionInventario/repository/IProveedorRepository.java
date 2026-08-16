package com.gestionInventario.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Proveedor;

@Repository
public interface IProveedorRepository extends JpaRepository<Proveedor, Long> , JpaSpecificationExecutor<Proveedor>{

}

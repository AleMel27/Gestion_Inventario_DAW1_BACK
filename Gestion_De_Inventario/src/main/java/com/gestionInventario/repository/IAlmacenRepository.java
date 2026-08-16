package com.gestionInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Almacen;

@Repository
public interface IAlmacenRepository extends JpaRepository<Almacen, Long>, JpaSpecificationExecutor<Almacen>{

}
package com.gestionInventario.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gestionInventario.model.Inventario;

@Repository
public interface IInventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoIdProductoAndAlmacenIdAlmacen(Long idProducto, Long idAlmacen);
}
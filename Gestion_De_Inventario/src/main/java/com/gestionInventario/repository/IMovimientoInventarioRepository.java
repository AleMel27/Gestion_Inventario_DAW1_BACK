package com.gestionInventario.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.MovimientoInventario;

@Repository
public interface IMovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    @Override
    @EntityGraph(attributePaths = {"producto", "almacen", "usuario", "tipoMovimiento", "compra"})
    Page<MovimientoInventario> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"producto", "almacen", "usuario", "tipoMovimiento", "compra"})
    Optional<MovimientoInventario> findById(Long id);

    @EntityGraph(attributePaths = {"producto", "almacen", "usuario", "tipoMovimiento", "compra"})
    Page<MovimientoInventario> findByProducto_IdProductoAndAlmacen_IdAlmacen(
            Long idProducto,
            Long idAlmacen,
            Pageable pageable);
}

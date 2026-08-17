package com.gestionInventario.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import com.gestionInventario.model.DetalleCompra;

@Repository
public interface IDetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    @EntityGraph(attributePaths = {"producto"})
    List<DetalleCompra> findByCompra_IdCompra(Long idCompra);
}

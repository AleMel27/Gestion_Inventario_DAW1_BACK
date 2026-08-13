package com.gestionInventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestionInventario.model.Inventario;

@Repository
public interface IInventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoIdProductoAndAlmacenIdAlmacen(Long idProducto, Long idAlmacen);

    // Listar inventario por almacén
    List<Inventario> findByAlmacenIdAlmacen(Long idAlmacen);

    // Listar inventario por almacén con paginación
    Page<Inventario> findByAlmacenIdAlmacen(Long idAlmacen, Pageable pageable);

    // Alertas: Productos con stock actual menor o igual al stock mínimo configurado
    // (Asumiendo que el campo stockMinimo pertenece a la entidad Producto)
    @Query("SELECT i FROM Inventario i WHERE i.stockActual <= i.producto.stockMinimo AND i.producto.estado = true")
    List<Inventario> obtenerAlertasStockBajo();

    // Alertas filtradas por almacén
    @Query("SELECT i FROM Inventario i WHERE i.almacen.idAlmacen = :idAlmacen AND i.stockActual <= i.producto.stockMinimo AND i.producto.estado = true")
    List<Inventario> obtenerAlertasStockBajoPorAlmacen(@Param("idAlmacen") Long idAlmacen);
}
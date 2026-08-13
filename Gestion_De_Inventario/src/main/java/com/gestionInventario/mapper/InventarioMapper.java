package com.gestionInventario.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.response.InventarioDTO;
import com.gestionInventario.model.Inventario;

@Component
public class InventarioMapper {

    public InventarioDTO convertirADto(Inventario inventario) {
        if (inventario == null) {
            return null;
        }

        // Evaluar la alerta de stock bajo
        boolean tieneAlerta = false;
        if (inventario.getProducto() != null 
                && inventario.getProducto().getStockMinimo() != null 
                && inventario.getStockActual() != null) {
            
            BigDecimal stockMinimo = inventario.getProducto().getStockMinimo();
            tieneAlerta = inventario.getStockActual().compareTo(stockMinimo) <= 0;
        }

        return InventarioDTO.builder()
                .idInventario(inventario.getIdInventario())
                
                // Mapeo Producto
                .idProducto(inventario.getProducto() != null ? inventario.getProducto().getIdProducto() : null)
                .codigoProducto(inventario.getProducto() != null ? inventario.getProducto().getCodigo() : null)
                .nombreProducto(inventario.getProducto() != null ? inventario.getProducto().getNombre() : null)
                .stockMinimoProducto(inventario.getProducto() != null ? inventario.getProducto().getStockMinimo() : null)
                
                // Mapeo Almacén
                .idAlmacen(inventario.getAlmacen() != null ? inventario.getAlmacen().getIdAlmacen() : null)
                .nombreAlmacen(inventario.getAlmacen() != null ? inventario.getAlmacen().getNombre() : null)
                
                // Stock y Alertas
                .stockActual(inventario.getStockActual())
                .alertaStockBajo(tieneAlerta)
                .fechaActualizacion(inventario.getFechaActualizacion())
                .build();
    }
}
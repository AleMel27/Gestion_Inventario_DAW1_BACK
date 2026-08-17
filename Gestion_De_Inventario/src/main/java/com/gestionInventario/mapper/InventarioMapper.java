package com.gestionInventario.mapper;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.request.InventarioCreateDTO;
import com.gestionInventario.dtos.request.InventarioUpdateDTO;
import com.gestionInventario.dtos.response.InventarioDTO;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.model.Inventario;
import com.gestionInventario.model.Producto;

@Component
public class InventarioMapper {

    public InventarioDTO convertirADto(Inventario inventario) {
        if (inventario == null) {
            return null;
        }

        boolean tieneAlerta = false;
        if (inventario.getProducto() != null 
                && inventario.getProducto().getStockMinimo() != null 
                && inventario.getStockActual() != null) {
            
            BigDecimal stockMinimo = inventario.getProducto().getStockMinimo();
            tieneAlerta = inventario.getStockActual().compareTo(stockMinimo) <= 0;
        }

        return InventarioDTO.builder()
                .idInventario(inventario.getIdInventario())
                .idProducto(inventario.getProducto() != null ? inventario.getProducto().getIdProducto() : null)
                .codigoProducto(inventario.getProducto() != null ? inventario.getProducto().getCodigo() : null)
                .nombreProducto(inventario.getProducto() != null ? inventario.getProducto().getNombre() : null)
                .stockMinimoProducto(inventario.getProducto() != null ? inventario.getProducto().getStockMinimo() : null)
                .idAlmacen(inventario.getAlmacen() != null ? inventario.getAlmacen().getIdAlmacen() : null)
                .nombreAlmacen(inventario.getAlmacen() != null ? inventario.getAlmacen().getNombre() : null)
                .stockActual(inventario.getStockActual())
                .alertaStockBajo(tieneAlerta)
                .fechaActualizacion(inventario.getFechaActualizacion())
                .build();
    }

    public Inventario convertirAEntidad(InventarioCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Inventario inventario = new Inventario();
        inventario.setStockActual(dto.getStockActual());

        if (dto.getIdProducto() != null) {
            Producto producto = new Producto();
            producto.setIdProducto(dto.getIdProducto());
            inventario.setProducto(producto);
        }

        if (dto.getIdAlmacen() != null) {
            Almacen almacen = new Almacen();
            almacen.setIdAlmacen(dto.getIdAlmacen());
            inventario.setAlmacen(almacen);
        }

        return inventario;
    }

    public void actualizarEntidadDesdeDto(InventarioUpdateDTO dto, Inventario inventario) {
        if (dto == null || inventario == null) {
            return;
        }
        inventario.setStockActual(dto.getStockActual());
    }
}
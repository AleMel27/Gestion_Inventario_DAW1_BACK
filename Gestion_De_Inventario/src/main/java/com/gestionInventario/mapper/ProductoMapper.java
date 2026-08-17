package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.request.ProductoCreateDTO;
import com.gestionInventario.dtos.request.ProductoUpdateDTO;
import com.gestionInventario.dtos.response.ProductoCategoriaDTO;
import com.gestionInventario.dtos.response.ProductoDTO;
import com.gestionInventario.dtos.response.ProductoUnidadMedidaDTO;
import com.gestionInventario.model.Categoria;
import com.gestionInventario.model.Producto;
import com.gestionInventario.model.UnidadMedida;

@Component
public class ProductoMapper {

    public ProductoDTO convertirADto(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setEstado(producto.getEstado());
        
        // Mapeo de Unidad de Medida a ProductoUnidadMedidaDTO
        dto.setUnidadMedida(convertirUnidadMedidaADto(producto.getUnidadMedida()));

        // Mapeo de Categoría
        if (producto.getCategoria() != null) {
            ProductoCategoriaDTO categoriaDTO = new ProductoCategoriaDTO();
            categoriaDTO.setIdCategoria(producto.getCategoria().getIdCategoria());
            categoriaDTO.setNombre(producto.getCategoria().getNombre());
            dto.setCategoria(categoriaDTO);
        }

        return dto;
    }

    public Producto convertirAEntidad(ProductoCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Producto producto = new Producto();
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(crearUnidadMedidaConId(dto.getIdUnidadMedida()));
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setEstado(true);

        if (dto.getIdCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(dto.getIdCategoria());
            producto.setCategoria(categoria);
        }

        return producto;
    }

    public void actualizarEntidadDesdeDto(ProductoUpdateDTO dto, Producto producto) {
        if (dto == null || producto == null) {
            return;
        }

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(crearUnidadMedidaConId(dto.getIdUnidadMedida()));
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStockMinimo(dto.getStockMinimo());

        if (dto.getIdCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(dto.getIdCategoria());
            producto.setCategoria(categoria);
        } else {
            producto.setCategoria(null);
        }
    }

    private ProductoUnidadMedidaDTO convertirUnidadMedidaADto(UnidadMedida unidadMedida) {
        if (unidadMedida == null) {
            return null;
        }

        ProductoUnidadMedidaDTO dto = new ProductoUnidadMedidaDTO();
        dto.setIdUnidadMedida(unidadMedida.getIdUnidadMedida());
        dto.setNombre(unidadMedida.getNombre());
        return dto;
    }

    private UnidadMedida crearUnidadMedidaConId(Integer idUnidadMedida) {
        if (idUnidadMedida == null) {
            return null;
        }

        UnidadMedida unidadMedida = new UnidadMedida();
        unidadMedida.setIdUnidadMedida(idUnidadMedida);
        return unidadMedida;
    }
}
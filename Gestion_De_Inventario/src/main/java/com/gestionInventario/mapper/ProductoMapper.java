package com.gestionInventario.mapper;

import org.springframework.stereotype.Component;

import com.gestionInventario.dtos.request.ProductoCreateDTO;
import com.gestionInventario.dtos.request.ProductoUpdateDTO;
import com.gestionInventario.dtos.response.ProductoCategoriaDTO;
import com.gestionInventario.dtos.response.ProductoDTO;
import com.gestionInventario.dtos.response.UnidadMedidaDTO;
import com.gestionInventario.model.Categoria;
import com.gestionInventario.model.Producto;
import com.gestionInventario.model.UnidadMedida;

@Component
public class ProductoMapper {

    public ProductoDTO convertirADto(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setUnidadMedida(convertirUnidadMedidaADto(producto.getUnidadMedida()));
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setEstado(producto.getEstado());

        if (producto.getCategoria() != null) {
            ProductoCategoriaDTO categoriaDTO = new ProductoCategoriaDTO();
            categoriaDTO.setIdCategoria(producto.getCategoria().getIdCategoria());
            categoriaDTO.setNombre(producto.getCategoria().getNombre());
            dto.setCategoria(categoriaDTO);
        }

        return dto;
    }

    public Producto convertirAEntidad(ProductoCreateDTO dto) {
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

    public Producto convertirAEntidad(ProductoUpdateDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(crearUnidadMedidaConId(dto.getIdUnidadMedida()));
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStockMinimo(dto.getStockMinimo());

        if (dto.getIdCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(dto.getIdCategoria());
            producto.setCategoria(categoria);
        }

        return producto;
    }

    private UnidadMedidaDTO convertirUnidadMedidaADto(UnidadMedida unidadMedida) {
        if (unidadMedida == null) {
            return null;
        }

        UnidadMedidaDTO dto = new UnidadMedidaDTO();
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

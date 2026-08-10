package com.gestionInventario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gestionInventario.model.Categoria;
import com.gestionInventario.model.Producto;
import com.gestionInventario.model.UnidadMedida;
import com.gestionInventario.repository.ICategoriaRepository;
import com.gestionInventario.repository.IProductoRepository;
import com.gestionInventario.repository.IUnidadMedidaRepository;

@Service
public class ProductoService {

    @Autowired
    private IProductoRepository repo;

    @Autowired
    private ICategoriaRepository categoriaRepo;

    @Autowired
    private IUnidadMedidaRepository unidadMedidaRepo;

    public Page<Producto> listarConFiltros(
            Boolean estado,
            String nombre,
            String codigo,
            Integer idUnidadMedida,
            Pageable pageable) {
        Specification<Producto> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado"), estado);

        if (tieneTexto(nombre)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("nombre")),
                            "%" + nombre.trim().toLowerCase() + "%"));
        }

        if (tieneTexto(codigo)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("codigo")),
                            "%" + codigo.trim().toLowerCase() + "%"));
        }

        if (idUnidadMedida != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("unidadMedida").get("idUnidadMedida"), idUnidadMedida));
        }

        return repo.findAll(spec, pageable);
    }

    public Producto obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Producto registrar(Producto producto) {
        asignarCategoriaExistente(producto);
        asignarUnidadMedidaExistente(producto);
        return repo.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        Producto productoExistente = repo.findById(id).orElse(null);

        if (productoExistente != null) {
            if (tieneTexto(producto.getNombre())) {
                productoExistente.setNombre(producto.getNombre());
            }

            if (tieneTexto(producto.getDescripcion())) {
                productoExistente.setDescripcion(producto.getDescripcion());
            }

            if (producto.getPrecioVenta() != null) {
                productoExistente.setPrecioVenta(producto.getPrecioVenta());
            }

            if (producto.getStockMinimo() != null) {
                productoExistente.setStockMinimo(producto.getStockMinimo());
            }

            if (producto.getCategoria() != null && producto.getCategoria().getIdCategoria() != null) {
                productoExistente.setCategoria(obtenerCategoriaExistente(producto.getCategoria().getIdCategoria()));
            }

            if (producto.getUnidadMedida() != null && producto.getUnidadMedida().getIdUnidadMedida() != null) {
                productoExistente.setUnidadMedida(
                        obtenerUnidadMedidaExistente(producto.getUnidadMedida().getIdUnidadMedida()));
            }

            return repo.save(productoExistente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        Producto productoExistente = repo.findById(id).orElse(null);

        if (productoExistente == null) {
            return false;
        }

        productoExistente.setEstado(false);
        repo.save(productoExistente);
        return true;
    }

    public boolean reactivar(Long id) {
        Producto productoExistente = repo.findById(id).orElse(null);

        if (productoExistente == null) {
            return false;
        }

        productoExistente.setEstado(true);
        repo.save(productoExistente);
        return true;
    }

    private void asignarCategoriaExistente(Producto producto) {
        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        producto.setCategoria(obtenerCategoriaExistente(producto.getCategoria().getIdCategoria()));
    }

    private void asignarUnidadMedidaExistente(Producto producto) {
        if (producto.getUnidadMedida() == null || producto.getUnidadMedida().getIdUnidadMedida() == null) {
            throw new RuntimeException("La unidad de medida es obligatoria");
        }

        producto.setUnidadMedida(obtenerUnidadMedidaExistente(producto.getUnidadMedida().getIdUnidadMedida()));
    }

    private Categoria obtenerCategoriaExistente(Long idCategoria) {
        Categoria categoria = categoriaRepo.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (Boolean.FALSE.equals(categoria.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede asignar una categoría inactiva al producto");
        }

        return categoria;
    }

    private UnidadMedida obtenerUnidadMedidaExistente(Integer idUnidadMedida) {
        return unidadMedidaRepo.findById(idUnidadMedida)
                .orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada"));
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

}

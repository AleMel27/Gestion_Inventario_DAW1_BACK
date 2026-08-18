package com.gestionInventario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gestionInventario.exception.ResourceNotFoundException;
import com.gestionInventario.model.Categoria;
import com.gestionInventario.repository.ICategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private ICategoriaRepository repo;

    public Page<Categoria> listarConFiltros(Boolean estado, String nombre, Pageable pageable) {
        Specification<Categoria> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado"), estado);

        if (tieneTexto(nombre)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("nombre")),
                            "%" + nombre.trim().toLowerCase() + "%"));
        }

        return repo.findAll(spec, pageable);
    }

    public Categoria obtenerPorId(Long id) {
        return obtenerCategoriaExistente(id);
    }

    public Categoria registrar(Categoria categoria) {
        return repo.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria categoria) {
        Categoria categoriaExistente = obtenerCategoriaExistente(id);

        if (tieneTexto(categoria.getNombre())) {
            categoriaExistente.setNombre(categoria.getNombre());
        }

        if (tieneTexto(categoria.getDescripcion())) {
            categoriaExistente.setDescripcion(categoria.getDescripcion());
        }

        return repo.save(categoriaExistente);
    }

    public boolean eliminar(Long id) {
        Categoria categoriaExistente = obtenerCategoriaExistente(id);

        categoriaExistente.setEstado(false);
        repo.save(categoriaExistente);
        return true;
    }

    public boolean reactivar(Long id) {
        Categoria categoriaExistente = obtenerCategoriaExistente(id);

        categoriaExistente.setEstado(true);
        repo.save(categoriaExistente);
        return true;
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private Categoria obtenerCategoriaExistente(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe"));
    }

}

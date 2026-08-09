package com.gestionInventario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Categoria;
import com.gestionInventario.repository.ICategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private ICategoriaRepository repo;

    public Page<Categoria> listarPorEstado(Boolean estado, Pageable pageable) {
        return repo.findByEstado(estado, pageable);
    }

    public Categoria obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Categoria registrar(Categoria categoria) {
        return repo.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria categoria) {
        Categoria categoriaExistente = repo.findById(id).orElse(null);

        if (categoriaExistente != null) {
            if (tieneTexto(categoria.getNombre())) {
                categoriaExistente.setNombre(categoria.getNombre());
            }

            if (tieneTexto(categoria.getDescripcion())) {
                categoriaExistente.setDescripcion(categoria.getDescripcion());
            }

            return repo.save(categoriaExistente);
        }
        return null;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

}

package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Categoria;
import com.gestionInventario.repository.ICategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private ICategoriaRepository repo;

    public List<Categoria> listarTodos() {
        return repo.findAll();
    }

    public Categoria obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Categoria registrar(Categoria categoria) {
        return repo.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria categoria) {
        if (repo.existsById(id)) {
            categoria.setIdCategoria(id);
            return repo.save(categoria);
        }
        return null;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

}
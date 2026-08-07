package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Producto;
import com.gestionInventario.repository.IProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private IProductoRepository repo;

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Producto registrar(Producto producto) {
        return repo.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        if (repo.existsById(id)) {
            producto.setIdProducto(id);
            return repo.save(producto);
        }
        return null;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

}
package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Proveedor;
import com.gestionInventario.repository.IProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private IProveedorRepository repo;

    public List<Proveedor> listarTodos() {
        return repo.findAll();
    }

    public Proveedor obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Proveedor registrar(Proveedor proveedor) {
        return repo.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor proveedor) {
        if (repo.existsById(id)) {
            proveedor.setIdProveedor(id);
            return repo.save(proveedor);
        }
        return null;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

}
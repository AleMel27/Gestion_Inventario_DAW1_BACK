package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Almacen;
import com.gestionInventario.repository.IAlmacenRepository;

@Service
public class AlmacenService {

    @Autowired
    private IAlmacenRepository repo;

    public List<Almacen> listarTodos() {
        return repo.findAll();
    }

    public Almacen obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Almacen registrar(Almacen almacen) {
        return repo.save(almacen);
    }

    public Almacen actualizar(Long id, Almacen almacen) {
        if (repo.existsById(id)) {
            almacen.setIdAlmacen(id);
            return repo.save(almacen);
        }
        return null;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

}
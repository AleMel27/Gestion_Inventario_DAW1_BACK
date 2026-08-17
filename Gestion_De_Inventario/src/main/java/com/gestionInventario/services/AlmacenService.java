package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionInventario.model.Almacen;
import com.gestionInventario.repository.IAlmacenRepository;

@Service
public class AlmacenService {

    @Autowired
    private IAlmacenRepository repo;

    @Transactional(readOnly = true)
    public Page<Almacen> listarConFiltros(Boolean estado, String nombre, Pageable pageable) {
        Specification<Almacen> spec = Specification.allOf();

        if (estado != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }

        if (nombre != null && !nombre.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("nombre")), "%" + nombre.trim().toLowerCase() + "%")
            );
        }

        return repo.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<Almacen> listarTodos() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Almacen obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional
    public Almacen registrar(Almacen almacen) {
        almacen.setEstado(true);
        return repo.save(almacen);
    }

    @Transactional
    public Almacen actualizar(Long id, Almacen almacenExistente) {
        if (repo.existsById(id)) {
            almacenExistente.setIdAlmacen(id);
            return repo.save(almacenExistente);
        }
        return null;
    }

    // ELIMINACIÓN LÓGICA
    @Transactional
    public boolean eliminarLogico(Long id) {
        Almacen almacen = obtenerPorId(id);
        if (almacen != null) {
            almacen.setEstado(false);
            repo.save(almacen);
            return true;
        }
        return false;
    }

    // REACTIVACIÓN
    @Transactional
    public boolean reactivar(Long id) {
        Almacen almacen = obtenerPorId(id);
        if (almacen != null) {
            almacen.setEstado(true);
            repo.save(almacen);
            return true;
        }
        return false;
    }
}
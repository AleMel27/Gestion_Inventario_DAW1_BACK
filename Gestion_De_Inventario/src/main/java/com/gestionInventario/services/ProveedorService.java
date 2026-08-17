package com.gestionInventario.services;

import java.util.List;

import org.springframework.data.domain.Page; // AGREGADO
import org.springframework.data.domain.Pageable; // AGREGADO
import org.springframework.data.jpa.domain.Specification; // AGREGADO
import org.springframework.util.StringUtils; // AGREGADO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Proveedor;
import com.gestionInventario.repository.IProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private IProveedorRepository repo;

 // =========================================================================
    // CORREGIDO: Filtro por razonSocial o ruc (coincide con el modelo Proveedor) -------- HECHO
    // =========================================================================
    public Page<Proveedor> listarConFiltros(String buscar, Pageable pageable) {
        Specification<Proveedor> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(buscar)) {
            String filtro = "%" + buscar.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("razonSocial")), filtro), 
                cb.like(cb.lower(root.get("ruc")), filtro)          
            ));
        }

        return repo.findAll(spec, pageable);
    }
    // =========================================================================
    
    
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
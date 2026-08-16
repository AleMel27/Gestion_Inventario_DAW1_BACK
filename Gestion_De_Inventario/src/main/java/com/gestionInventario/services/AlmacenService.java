package com.gestionInventario.services;

import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;




import com.gestionInventario.model.Almacen;
import com.gestionInventario.repository.IAlmacenRepository;

@Service
public class AlmacenService {

    @Autowired
    private IAlmacenRepository repo;
    
    
    
    
    
 // ==========================================
    // NUEVO: Método agregado para el paginado --------------- HECHO
    // ==========================================
    
    
    public Page<Almacen> listarConFiltros(Boolean estado, String nombre, Pageable pageable) {
        Specification<Almacen> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado"), estado);

        if (nombre != null && !nombre.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("nombre")),
                            "%" + nombre.trim().toLowerCase() + "%"));
        }

        return repo.findAll(spec, pageable);
    }
    // ==========================================
    
    
    
    

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
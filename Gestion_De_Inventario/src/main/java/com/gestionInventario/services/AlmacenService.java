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

	// ==========================================
	// NUEVO: Método agregado para el paginado --------------- HECHO
	// ==========================================

	@Transactional(readOnly = true)
	public Page<Almacen> listarConFiltros(Boolean estado, String nombre, Pageable pageable) {
	    // Especificación neutra inicial (evita el problema de ambigüedad con null)
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
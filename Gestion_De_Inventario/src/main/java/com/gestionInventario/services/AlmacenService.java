package com.gestionInventario.services;

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

    public Page<Almacen> listarConFiltros(
            Boolean estado,
            String nombre,
            String ubicacion,
            Pageable pageable) {
        Specification<Almacen> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado"), estado);

        if (tieneTexto(nombre)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("nombre")),
                            "%" + nombre.trim().toLowerCase() + "%"));
        }

        if (tieneTexto(ubicacion)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("ubicacion")),
                            "%" + ubicacion.trim().toLowerCase() + "%"));
        }

        return repo.findAll(spec, pageable);
    }

    public Almacen obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Almacen registrar(Almacen almacen) {
        almacen.setEstado(true);
        return repo.save(almacen);
    }

    public Almacen actualizar(Long id, Almacen almacen) {
        Almacen almacenExistente = repo.findById(id).orElse(null);

        if (almacenExistente != null) {
            if (tieneTexto(almacen.getNombre())) {
                almacenExistente.setNombre(almacen.getNombre());
            }

            if (tieneTexto(almacen.getUbicacion())) {
                almacenExistente.setUbicacion(almacen.getUbicacion());
            }

            if (tieneTexto(almacen.getDescripcion())) {
                almacenExistente.setDescripcion(almacen.getDescripcion());
            }

            return repo.save(almacenExistente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        Almacen almacenExistente = repo.findById(id).orElse(null);

        if (almacenExistente == null) {
            return false;
        }

        almacenExistente.setEstado(false);
        repo.save(almacenExistente);
        return true;
    }

    public boolean reactivar(Long id) {
        Almacen almacenExistente = repo.findById(id).orElse(null);

        if (almacenExistente == null) {
            return false;
        }

        almacenExistente.setEstado(true);
        repo.save(almacenExistente);
        return true;
    }
    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
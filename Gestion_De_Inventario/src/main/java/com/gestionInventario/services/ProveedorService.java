package com.gestionInventario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gestionInventario.model.Proveedor;
import com.gestionInventario.repository.IProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private IProveedorRepository repo;

    public Page<Proveedor> listarConFiltros(
            Boolean estado,
            String razonSocial,
            String ruc,
            String telefono,
            Pageable pageable) {

        // Base: filtro por estado si viene presente
        Specification<Proveedor> spec = (root, query, criteriaBuilder) -> {
            if (estado != null) {
                return criteriaBuilder.equal(root.get("estado"), estado);
            }
            return criteriaBuilder.conjunction();
        };

        // Filtro opcional por Razón Social
        if (tieneTexto(razonSocial)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("razonSocial")),
                            "%" + razonSocial.trim().toLowerCase() + "%"));
        }

        // Filtro opcional por RUC
        if (tieneTexto(ruc)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("ruc")),
                            "%" + ruc.trim().toLowerCase() + "%"));
        }

        // Filtro opcional por Teléfono
        if (tieneTexto(telefono)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("telefono")),
                            "%" + telefono.trim().toLowerCase() + "%"));
        }

        return repo.findAll(spec, pageable);
    }

    public Proveedor obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Proveedor registrar(Proveedor proveedor) {
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(true);
        }
        return repo.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor proveedor) {
        Proveedor proveedorExistente = repo.findById(id).orElse(null);

        if (proveedorExistente != null) {
            if (tieneTexto(proveedor.getRuc())) {
                proveedorExistente.setRuc(proveedor.getRuc());
            }

            if (tieneTexto(proveedor.getRazonSocial())) {
                proveedorExistente.setRazonSocial(proveedor.getRazonSocial());
            }

            if (tieneTexto(proveedor.getTelefono())) {
                proveedorExistente.setTelefono(proveedor.getTelefono());
            }

            if (tieneTexto(proveedor.getCorreo())) {
                proveedorExistente.setCorreo(proveedor.getCorreo());
            }

            if (tieneTexto(proveedor.getDireccion())) {
                proveedorExistente.setDireccion(proveedor.getDireccion());
            }

            return repo.save(proveedorExistente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        Proveedor proveedorExistente = repo.findById(id).orElse(null);

        if (proveedorExistente == null) {
            return false;
        }

        proveedorExistente.setEstado(false);
        repo.save(proveedorExistente);
        return true;
    }

    public boolean reactivar(Long id) {
        Proveedor proveedorExistente = repo.findById(id).orElse(null);

        if (proveedorExistente == null) {
            return false;
        }

        proveedorExistente.setEstado(true);
        repo.save(proveedorExistente);
        return true;
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
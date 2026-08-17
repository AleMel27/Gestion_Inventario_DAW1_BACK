package com.gestionInventario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.gestionInventario.dtos.request.ProveedorCreateDTO;
import com.gestionInventario.dtos.request.ProveedorUpdateDTO;
import com.gestionInventario.dtos.response.ProveedorDTO;
import com.gestionInventario.model.Proveedor;
import com.gestionInventario.repository.IProveedorRepository;

@Service
public class ProveedorService {

	@Autowired
	private IProveedorRepository repo;

	@Transactional(readOnly = true)
	public Page<ProveedorDTO> listarConFiltros(String buscar, Pageable pageable) {
		Specification<Proveedor> spec = (root, query, cb) -> cb.equal(root.get("estado"), true);

		if (StringUtils.hasText(buscar)) {
			String filtro = "%" + buscar.trim().toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.or(cb.like(cb.lower(root.get("razonSocial")), filtro),
					cb.like(cb.lower(root.get("ruc")), filtro)));
		}

		return repo.findAll(spec, pageable).map(this::convertirADto);
	}

	@Transactional(readOnly = true)
	public List<ProveedorDTO> listarTodos() {
		// Si necesitas listar únicamente los activos sin paginado:
		return repo.findAll().stream().filter(Proveedor::getEstado) // Solo estado = true
				.map(this::convertirADto).toList();
	}

	@Transactional(readOnly = true)
	public ProveedorDTO obtenerPorId(Long id) {
		return repo.findById(id).filter(Proveedor::getEstado) // Garantiza que no se devuelva si está inactivo
				.map(this::convertirADto).orElse(null);
	}

	@Transactional
	public ProveedorDTO registrar(ProveedorCreateDTO dto) {
		Proveedor proveedor = new Proveedor();
		proveedor.setRuc(dto.getRuc());
		proveedor.setRazonSocial(dto.getRazonSocial());
		proveedor.setTelefono(dto.getTelefono());
		proveedor.setCorreo(dto.getCorreo());
		proveedor.setDireccion(dto.getDireccion());
		proveedor.setEstado(true); // Activo por defecto

		Proveedor guardado = repo.save(proveedor);
		return convertirADto(guardado);
	}

	@Transactional
	public ProveedorDTO actualizar(Long id, ProveedorUpdateDTO dto) {
		return repo.findById(id).filter(Proveedor::getEstado) // No permite actualizar si fue eliminado lógicamente
				.map(proveedor -> {
					proveedor.setTelefono(dto.getTelefono());
					proveedor.setCorreo(dto.getCorreo());
					proveedor.setDireccion(dto.getDireccion());

					Proveedor actualizado = repo.save(proveedor);
					return convertirADto(actualizado);
				}).orElse(null);
	}

	// =========================================================================
	// BORRADO LÓGICO
	// =========================================================================
	@Transactional
	public boolean eliminarLogico(Long id) {
		return repo.findById(id).map(proveedor -> {
			proveedor.setEstado(false); // Cambiamos el estado a inactivo
			repo.save(proveedor); // Guardamos la actualización
			return true;
		}).orElse(false);
	}

	// =========================================================================
	// REACTIVACIÓN LÓGICA
	// =========================================================================
	@Transactional
	public boolean reactivar(Long id) {
		return repo.findById(id).map(proveedor -> {
			if (Boolean.TRUE.equals(proveedor.getEstado())) {
				// Ya estaba activo o no requiere reactivación
				return true;
			}
			proveedor.setEstado(true); // Cambiamos el estado a activo
			repo.save(proveedor); // Guardamos la actualización
			return true;
		}).orElse(false);
	}

	// =========================================================================
	// Mapeo manual
	// =========================================================================
	private ProveedorDTO convertirADto(Proveedor proveedor) {
		ProveedorDTO dto = new ProveedorDTO();
		dto.setIdProveedor(proveedor.getIdProveedor());
		dto.setRuc(proveedor.getRuc());
		dto.setRazonSocial(proveedor.getRazonSocial());
		dto.setTelefono(proveedor.getTelefono());
		dto.setCorreo(proveedor.getCorreo());
		dto.setDireccion(proveedor.getDireccion());
		dto.setEstado(proveedor.getEstado());
		return dto;
	}
}
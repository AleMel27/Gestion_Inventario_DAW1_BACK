package com.gestionInventario.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.gestionInventario.exception.BusinessRuleException;
import com.gestionInventario.exception.ResourceNotFoundException;
import com.gestionInventario.model.Almacen;
import com.gestionInventario.model.Inventario;
import com.gestionInventario.model.Producto;
import com.gestionInventario.repository.IAlmacenRepository;
import com.gestionInventario.repository.IInventarioRepository;
import com.gestionInventario.repository.IProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final IInventarioRepository inventarioRepo;
    private final IProductoRepository productoRepo;
    private final IAlmacenRepository almacenRepo;

    @Transactional(readOnly = true)
    public Inventario obtenerPorId(Long id) {
        return obtenerInventarioExistente(id);
    }

    @Transactional
    public Inventario registrar(Inventario inventario) {
        Long idProducto = inventario.getProducto().getIdProducto();
        Long idAlmacen = inventario.getAlmacen().getIdAlmacen();

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + idProducto));
        Almacen almacen = almacenRepo.findById(idAlmacen)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado: " + idAlmacen));

        inventario.setProducto(producto);
        inventario.setAlmacen(almacen);

        return inventarioRepo.save(inventario);
    }

    @Transactional
    public Inventario actualizar(Long id, Inventario inventario) {
        Inventario existente = obtenerInventarioExistente(id);

        if (inventario.getStockActual() != null) {
            existente.setStockActual(inventario.getStockActual());
        }
        return inventarioRepo.save(existente);
    }

    // =========================================================================
    // PAGINADO Y CONSULTAS DIVERSAS
    // =========================================================================

    @Transactional(readOnly = true)
    public Page<Inventario> listarConFiltros(Long idAlmacen, String nombreProducto, Pageable pageable) {
        Specification<Inventario> spec = (root, query, cb) -> cb.conjunction();

        if (idAlmacen != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("almacen").get("idAlmacen"), idAlmacen)
            );
        }

        if (StringUtils.hasText(nombreProducto)) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("producto").get("nombre")), "%" + nombreProducto.trim().toLowerCase() + "%")
            );
        }

        return inventarioRepo.findAll(spec, pageable);
    }

    // Listar todo el inventario
    @Transactional(readOnly = true)
    public List<Inventario> listarTodos() {
        return inventarioRepo.findAll();
    }

    // Listar existencias por ID de almacén
    @Transactional(readOnly = true)
    public List<Inventario> listarPorAlmacen(Long idAlmacen) {
        if (!almacenRepo.existsById(idAlmacen)) {
            throw new ResourceNotFoundException("El almacén especificado no existe: " + idAlmacen);
        }
        return inventarioRepo.findByAlmacenIdAlmacen(idAlmacen);
    }

    // Listar existencias por almacén con paginación
    @Transactional(readOnly = true)
    public Page<Inventario> listarPorAlmacenPaginado(Long idAlmacen, Pageable pageable) {
        if (!almacenRepo.existsById(idAlmacen)) {
            throw new ResourceNotFoundException("El almacén especificado no existe: " + idAlmacen);
        }
        return inventarioRepo.findByAlmacenIdAlmacen(idAlmacen, pageable);
    }

    // Obtener el registro de inventario específico de un producto en un almacén
    @Transactional(readOnly = true)
    public Inventario obtenerPorProductoYAlmacen(Long idProducto, Long idAlmacen) {
        return inventarioRepo.findByProductoIdProductoAndAlmacenIdAlmacen(idProducto, idAlmacen)
                .orElseThrow(() -> new ResourceNotFoundException("No existe registro de inventario para el producto " 
                        + idProducto + " en el almacén " + idAlmacen));
    }

    // Obtener el stock actual en formato BigDecimal (retorna 0 si no existe el registro)
    @Transactional(readOnly = true)
    public BigDecimal consultarStockActual(Long idProducto, Long idAlmacen) {
        return inventarioRepo.findByProductoIdProductoAndAlmacenIdAlmacen(idProducto, idAlmacen)
                .map(Inventario::getStockActual)
                .orElse(BigDecimal.ZERO);
    }

    // Alertas de productos con stock bajo (global)
    @Transactional(readOnly = true)
    public List<Inventario> obtenerAlertasStockBajo() {
        return inventarioRepo.obtenerAlertasStockBajo();
    }

    // Alertas de productos con stock bajo por almacén
    @Transactional(readOnly = true)
    public List<Inventario> obtenerAlertasStockBajoPorAlmacen(Long idAlmacen) {
        if (!almacenRepo.existsById(idAlmacen)) {
            throw new ResourceNotFoundException("El almacén especificado no existe: " + idAlmacen);
        }
        return inventarioRepo.obtenerAlertasStockBajoPorAlmacen(idAlmacen);
    }

    // Actualización directa o inicialización de stock
    @Transactional
    public Inventario actualizarStockDirecto(Long idProducto, Long idAlmacen, BigDecimal nuevoStock) {
        if (nuevoStock == null || nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("El stock no puede ser un valor negativo");
        }

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + idProducto));

        Almacen almacen = almacenRepo.findById(idAlmacen)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado: " + idAlmacen));

        Inventario inventario = inventarioRepo
                .findByProductoIdProductoAndAlmacenIdAlmacen(idProducto, idAlmacen)
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setProducto(producto);
                    nuevo.setAlmacen(almacen);
                    return nuevo;
                });

        inventario.setStockActual(nuevoStock);
        return inventarioRepo.save(inventario);
    }

    private Inventario obtenerInventarioExistente(Long id) {
        return inventarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El inventario no existe"));
    }
}

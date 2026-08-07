package com.gestionInventario.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.inventario.enums.UnidadMedida;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "productos", uniqueConstraints = {
		@UniqueConstraint(name = "uq_productos_codigo", columnNames = "codigo") })
@Data
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_producto")
	private Long idProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria", nullable = false)
	private Categoria categoria;

	@Column(nullable = false, unique = true, length = 50)
	private String codigo;

	@Column(nullable = false, length = 150)
	private String nombre;

	@Column(length = 500)
	private String descripcion;

	@Enumerated(EnumType.STRING)
	@Column(name = "unidad_medida", nullable = false)
	private UnidadMedida unidadMedida;

	@Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioVenta;

	@Column(name = "stock_minimo", nullable = false, precision = 12, scale = 3)
	private BigDecimal stockMinimo;

	@Column(nullable = false)
	private Boolean estado;

	@CreationTimestamp
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@UpdateTimestamp
	@Column(name = "fecha_actualizacion", nullable = false)
	private LocalDateTime fechaActualizacion;

}

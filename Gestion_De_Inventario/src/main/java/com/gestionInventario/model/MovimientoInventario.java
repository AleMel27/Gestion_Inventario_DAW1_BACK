package com.gestionInventario.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.inventario.enums.TipoMovimiento;

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
import lombok.Data;

@Entity
@Table(name = "movimientos_inventario")
@Data
public class MovimientoInventario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_movimiento")
	private Long idMovimiento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_producto", nullable = false)
	private Producto producto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_almacen", nullable = false)
	private Almacen almacen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compra")
	private Compra compra;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_movimiento", nullable = false)
	private TipoMovimiento tipoMovimiento;

	@Column(nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidad;

	@Column(name = "stock_anterior", nullable = false, precision = 12, scale = 3)
	private BigDecimal stockAnterior;

	@Column(name = "stock_posterior", nullable = false, precision = 12, scale = 3)
	private BigDecimal stockPosterior;

	@Column(nullable = false, length = 255)
	private String motivo;

	@Column(length = 100)
	private String referencia;

	@CreationTimestamp
	@Column(name = "fecha_movimiento", nullable = false, updatable = false)
	private LocalDateTime fechaMovimiento;

}

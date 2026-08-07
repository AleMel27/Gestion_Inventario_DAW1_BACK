package com.gestionInventario.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "detalle_compra")
@Data
public class DetalleCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle_compra")
	private Long idDetalleCompra;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compra", nullable = false)
	private Compra compra;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_producto", nullable = false)
	private Producto producto;

	@Column(nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidad;

	@Column(name = "costo_unitario", nullable = false, precision = 12, scale = 2)
	private BigDecimal costoUnitario;

	@Column(insertable = false, updatable = false, precision = 14, scale = 2)
	private BigDecimal subtotal;

}

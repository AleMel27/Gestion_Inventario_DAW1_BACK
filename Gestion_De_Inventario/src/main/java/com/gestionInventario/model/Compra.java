package com.gestionInventario.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.gestionInventario.enums.EstadoCompra;
import com.gestionInventario.enums.TipoComprobante;

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
@Table(name = "compras", uniqueConstraints = { @UniqueConstraint(name = "uq_compras_comprobante", columnNames = {
		"id_proveedor", "tipo_comprobante", "numero_comprobante" }) })
@Data
public class Compra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_compra")
	private Long idCompra;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proveedor", nullable = false)
	private Proveedor proveedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;

	@CreationTimestamp
	@Column(name = "fecha_compra", nullable = false, updatable = false)
	private LocalDateTime fechaCompra;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_comprobante", nullable = false)
	private TipoComprobante tipoComprobante;

	@Column(name = "numero_comprobante", nullable = false, length = 50)
	private String numeroComprobante;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoCompra estado;

	@Column(length = 500)
	private String observacion;

	@CreationTimestamp
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@UpdateTimestamp
	@Column(name = "fecha_actualizacion", nullable = false)
	private LocalDateTime fechaActualizacion;

}

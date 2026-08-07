package com.gestionInventario.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "almacenes")
@Data
public class Almacen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_almacen")
	private Long idAlmacen;

	@Column(nullable = false, length = 100, unique = true)
	private String nombre;

	@Column(nullable = false, length = 200)
	private String ubicacion;

	@Column(length = 255)
	private String descripcion;

	@Column(nullable = false)
	private Integer estado;

	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "fecha_actualizacion", nullable = false)
	private LocalDateTime fechaActualizacion;

}

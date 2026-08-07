package com.gestionInventario.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "proveedores", uniqueConstraints = { @UniqueConstraint(name = "uq_proveedores_ruc", columnNames = "ruc"),
		@UniqueConstraint(name = "uq_proveedores_correo", columnNames = "correo") })
@Data
public class Proveedor {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

	@NotBlank
	@Size(min = 11, max = 11)
	@Column(nullable = false, unique = true, length = 11)
	private String ruc;

	@NotBlank
	@Size(max = 150)
	@Column(name = "razon_social", nullable = false, length = 150)
	private String razonSocial;

    @Column(length = 20)
    private String telefono;

    @Email
    @Size(max = 150)
    @Column(unique = true, length = 150)
    private String correo;

    @Column(length = 255)
    private String direccion;

    @Column(nullable = false)
    private Boolean estado;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
	
}

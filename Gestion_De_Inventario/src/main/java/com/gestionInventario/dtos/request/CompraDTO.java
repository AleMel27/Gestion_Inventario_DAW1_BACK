package com.gestionInventario.dtos.request;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompraDTO {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long idProveedor;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El ID del almacén es obligatorio")
    private Long idAlmacen;

    @NotNull(message = "El ID del tipo de comprobante es obligatorio")
    private Short idTipoComprobante;

    @NotBlank(message = "El número de comprobante es obligatorio")
    private String numeroComprobante;

    private String observacion;

    @NotEmpty(message = "La compra debe incluir al menos un detalle")
    @Valid
    private List<DetalleCompraDTO> detalles;
}
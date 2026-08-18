package com.gestionInventario.dtos.request;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompraDTO {

    @NotNull(message = "El ID del proveedor es obligatorio")
    @Positive(message = "El ID del proveedor debe ser mayor a cero")
    private Long idProveedor;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser mayor a cero")
    private Long idUsuario;

    @NotNull(message = "El ID del almacén es obligatorio")
    @Positive(message = "El ID del almacén debe ser mayor a cero")
    private Long idAlmacen;

    @NotNull(message = "El ID del tipo de comprobante es obligatorio")
    @Positive(message = "El ID del tipo de comprobante debe ser mayor a cero")
    private Short idTipoComprobante;

    @NotBlank(message = "El número de comprobante es obligatorio")
    @Size(max = 50, message = "El número de comprobante no debe superar 50 caracteres")
    private String numeroComprobante;

    @Size(max = 500, message = "La observación no debe superar 500 caracteres")
    private String observacion;

    @NotEmpty(message = "La compra debe incluir al menos un detalle")
    private List<@Valid DetalleCompraDTO> detalles;
}

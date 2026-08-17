package com.gestionInventario.dtos.request;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 50, message = "El número de comprobante no debe superar 50 caracteres")
    private String numeroComprobante;

    @Size(max = 500, message = "La observación no debe superar 500 caracteres")
    private String observacion;

    @NotEmpty(message = "La compra debe incluir al menos un detalle")
    @Valid
    private List<DetalleCompraDTO> detalles;
}

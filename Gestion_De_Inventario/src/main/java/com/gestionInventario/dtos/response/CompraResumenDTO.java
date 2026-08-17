package com.gestionInventario.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.gestionInventario.dtos.response.CompraResDTO.AlmacenCompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO.ProveedorCompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO.TipoComprobanteCompraDTO;
import com.gestionInventario.dtos.response.CompraResDTO.UsuarioCompraDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResumenDTO {

    private Long idCompra;
    private ProveedorCompraDTO proveedor;
    private UsuarioCompraDTO usuario;
    private AlmacenCompraDTO almacen;
    private TipoComprobanteCompraDTO tipoComprobante;
    private LocalDateTime fechaCompra;
    private String numeroComprobante;
    private BigDecimal total;
    private String estado;
}

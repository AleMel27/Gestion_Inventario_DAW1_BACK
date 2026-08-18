package com.gestionInventario.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioResDTO {
    private Long idMovimientoInventario;
    private MovimientoProductoDTO producto;
    private MovimientoAlmacenDTO almacen;
    private MovimientoUsuarioDTO usuario;
    private MovimientoTipoDTO tipoMovimiento;
    private MovimientoCompraDTO compra;
    private BigDecimal cantidad;
    private BigDecimal stockAnterior;
    private BigDecimal stockPosterior;
    private String motivo;
    private String referencia;
    private LocalDateTime fechaMovimiento;
}

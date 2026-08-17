package com.gestionInventario.dtos.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class InventarioUpdateDTO {

    private BigDecimal stockActual;
}
package com.motorepuestos.inventario.DTOs.Request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequest {

    private String codigo;

    private String nombre;

    private Long categoriaId;

    private BigDecimal precioCompra;

    private BigDecimal precioVenta;

    private Integer stockInicial;

    private Boolean estado;
}

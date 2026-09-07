package com.motorepuestos.inventario.DTOs.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@RequiredArgsConstructor
public class ProductoResponse {

    private final Long id;

    private final String codigo;

    private final String nombre;

    private final Long categoriaId;

    private final BigDecimal precioCompra;

    private final BigDecimal precioVenta;

    private final Integer stockActual;

    private final Boolean estado;
}

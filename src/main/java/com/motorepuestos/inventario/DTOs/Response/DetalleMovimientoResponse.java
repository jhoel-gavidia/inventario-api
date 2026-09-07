package com.motorepuestos.inventario.DTOs.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class DetalleMovimientoResponse {

    private final Long productoId;

    private final String productoNombre;

    private final Integer cantidad;
}

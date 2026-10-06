package com.motorepuestos.inventario.movimiento.dto;

import com.motorepuestos.inventario.movimiento.entity.Tipo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class MovimientoResponse {

    private final Long id;

    private final Tipo tipo;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime fecha;

    private final List<DetalleMovimientoResponse> detalles;
}

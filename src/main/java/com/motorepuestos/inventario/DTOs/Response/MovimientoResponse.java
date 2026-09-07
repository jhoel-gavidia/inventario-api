package com.motorepuestos.inventario.DTOs.Response;

import com.motorepuestos.inventario.entity.Tipo;
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

    private final LocalDateTime fecha;

    private final List<DetalleMovimientoResponse> detalles;
}

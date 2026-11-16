package com.motorepuestos.inventario.movimiento.application.query;

import org.springframework.data.domain.Pageable;

public record BuscarMovimientoDetallesQuery(
        String tipo,
        String search,
        Pageable pageable
) {
}

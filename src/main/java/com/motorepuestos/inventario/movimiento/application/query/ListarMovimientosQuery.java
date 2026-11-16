package com.motorepuestos.inventario.movimiento.application.query;

import org.springframework.data.domain.Pageable;

public record ListarMovimientosQuery(Pageable pageable) {
}

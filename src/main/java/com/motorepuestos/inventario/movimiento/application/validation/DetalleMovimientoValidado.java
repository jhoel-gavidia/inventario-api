package com.motorepuestos.inventario.movimiento.application.validation;

import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;

public record DetalleMovimientoValidado(
        Long productoId,
        CantidadMovimiento cantidad
) {
}

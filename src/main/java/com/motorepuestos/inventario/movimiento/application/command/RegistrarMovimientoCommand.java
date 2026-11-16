package com.motorepuestos.inventario.movimiento.application.command;

import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import com.motorepuestos.inventario.movimiento.entity.Tipo;

import java.util.List;

public record RegistrarMovimientoCommand(
        Tipo tipo,
        List<DetalleMovimientoRequest> detalles
) {
}

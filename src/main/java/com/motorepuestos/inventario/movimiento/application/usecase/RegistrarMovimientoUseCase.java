package com.motorepuestos.inventario.movimiento.application.usecase;

import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;

public interface RegistrarMovimientoUseCase {

    MovimientoResponse ejecutar(RegistrarMovimientoCommand command);
}

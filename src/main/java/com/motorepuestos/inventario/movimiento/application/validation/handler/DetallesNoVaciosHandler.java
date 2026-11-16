package com.motorepuestos.inventario.movimiento.application.validation.handler;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionContext;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class DetallesNoVaciosHandler implements MovimientoValidacionHandler {

    @Override
    public void validar(MovimientoValidacionContext context) {
        if (context.getDetalles() == null || context.getDetalles().isEmpty()) {
            throw new BusinessException("El movimiento debe tener al menos un detalle");
        }
    }
}

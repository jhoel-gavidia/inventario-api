package com.motorepuestos.inventario.movimiento.application.validation.handler;

import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionContext;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(50)
public class OrdenarDetallesHandler implements MovimientoValidacionHandler {

    @Override
    public void validar(MovimientoValidacionContext context) {
        context.ordenarDetallesValidados();
    }
}

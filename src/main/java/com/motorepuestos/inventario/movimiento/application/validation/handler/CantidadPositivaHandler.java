package com.motorepuestos.inventario.movimiento.application.validation.handler;

import com.motorepuestos.inventario.movimiento.application.validation.DetalleMovimientoValidado;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionContext;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionHandler;
import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class CantidadPositivaHandler implements MovimientoValidacionHandler {

    @Override
    public void validar(MovimientoValidacionContext context) {
        for (DetalleMovimientoRequest detalle : context.getDetalles()) {
            CantidadMovimiento cantidad = CantidadMovimiento.de(detalle.getCantidad());
            context.agregarDetalleValidado(
                    new DetalleMovimientoValidado(detalle.getProductoId(), cantidad)
            );
        }
    }
}

package com.motorepuestos.inventario.movimiento.application.validation.handler;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionContext;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionHandler;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class ProductoIdObligatorioHandler implements MovimientoValidacionHandler {

    @Override
    public void validar(MovimientoValidacionContext context) {
        for (DetalleMovimientoRequest detalle : context.getDetalles()) {
            if (detalle.getProductoId() == null) {
                throw new BusinessException("El id del producto es obligatorio");
            }
        }
    }
}

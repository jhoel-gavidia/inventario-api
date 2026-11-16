package com.motorepuestos.inventario.movimiento.application.validation.handler;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionContext;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionHandler;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(30)
public class ProductosNoRepetidosHandler implements MovimientoValidacionHandler {

    @Override
    public void validar(MovimientoValidacionContext context) {
        Set<Long> productos = new HashSet<>();

        for (DetalleMovimientoRequest detalle : context.getDetalles()) {
            if (!productos.add(detalle.getProductoId())) {
                throw new BusinessException(
                        "El producto no puede repetirse dentro del mismo movimiento"
                );
            }
        }
    }
}

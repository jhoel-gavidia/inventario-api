package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import com.motorepuestos.inventario.movimiento.domain.factory.MovimientoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(40)
@RequiredArgsConstructor
public class ConstruirMovimientoStep implements MovimientoRegistroStep {

    private final MovimientoFactory movimientoFactory;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        context.setMovimiento(movimientoFactory.crear(
                context.getCommand().tipo(),
                context.getUsuario(),
                LocalDateTime.now(),
                context.getDetalles()
        ));
    }
}

package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import com.motorepuestos.inventario.movimiento.service.strategy.MovimientoTipoStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
@RequiredArgsConstructor
public class ResolverEstrategiaStep implements MovimientoRegistroStep {

    private final MovimientoTipoStrategyFactory movimientoTipoStrategyFactory;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        context.setEstrategia(
                movimientoTipoStrategyFactory.obtener(context.getCommand().tipo())
        );
    }
}

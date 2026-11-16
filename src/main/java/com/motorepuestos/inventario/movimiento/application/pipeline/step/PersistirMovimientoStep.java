package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.mapper.MovimientoMapper;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(50)
@RequiredArgsConstructor
public class PersistirMovimientoStep implements MovimientoRegistroStep {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        Movimiento movimientoGuardado = movimientoRepository.save(context.getMovimiento());

        context.setMovimiento(movimientoGuardado);
        context.setResponse(movimientoMapper.toResponse(movimientoGuardado));
    }
}

package com.motorepuestos.inventario.movimiento.application.usecase;

import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroPipeline;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarMovimientoUseCaseImpl implements RegistrarMovimientoUseCase {

    private final MovimientoRegistroPipeline movimientoRegistroPipeline;

    @Override
    @Transactional
    public MovimientoResponse ejecutar(RegistrarMovimientoCommand command) {
        return movimientoRegistroPipeline.ejecutar(command);
    }
}

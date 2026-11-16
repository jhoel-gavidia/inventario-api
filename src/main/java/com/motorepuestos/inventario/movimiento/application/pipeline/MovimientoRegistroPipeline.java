package com.motorepuestos.inventario.movimiento.application.pipeline;

import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovimientoRegistroPipeline {

    private final List<MovimientoRegistroStep> pasos;

    public MovimientoResponse ejecutar(RegistrarMovimientoCommand command) {
        MovimientoRegistroContext context = new MovimientoRegistroContext(command);
        pasos.forEach(paso -> paso.ejecutar(context));
        return context.getResponse();
    }
}

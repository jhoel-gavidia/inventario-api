package com.motorepuestos.inventario.movimiento.application.validation;

import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovimientoValidacionChain {

    private final List<MovimientoValidacionHandler> handlers;

    public List<DetalleMovimientoValidado> validar(List<DetalleMovimientoRequest> detalles) {
        MovimientoValidacionContext context = new MovimientoValidacionContext(detalles);
        handlers.forEach(handler -> handler.validar(context));
        return List.copyOf(context.getDetallesValidados());
    }
}

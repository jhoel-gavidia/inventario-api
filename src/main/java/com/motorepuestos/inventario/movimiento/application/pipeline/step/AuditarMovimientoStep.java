package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(60)
@RequiredArgsConstructor
public class AuditarMovimientoStep implements MovimientoRegistroStep {

    private final AuditoriaHelper auditoriaHelper;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        auditoriaHelper.registrarCreacion(
                "REGISTRAR",
                "MOVIMIENTO",
                context.getMovimiento().getId(),
                context.getResponse()
        );
    }
}

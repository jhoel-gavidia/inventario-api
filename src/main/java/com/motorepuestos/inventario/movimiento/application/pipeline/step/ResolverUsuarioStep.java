package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
@RequiredArgsConstructor
public class ResolverUsuarioStep implements MovimientoRegistroStep {

    private final SecurityUtils securityUtils;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        context.setUsuario(securityUtils.obtenerUsuarioAutenticado());
    }
}

package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.producto.domain.policy.StockBajoPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(70)
@RequiredArgsConstructor
public class PublicarAlertasStockStep implements MovimientoRegistroStep {

    private final StockBajoPolicy stockBajoPolicy;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        if (context.getCommand().tipo() != Tipo.SALIDA) {
            return;
        }

        context.getDetalles().stream()
                .map(DetalleMovimiento::getProducto)
                .map(stockBajoPolicy::evaluar)
                .forEach(alerta -> alerta.ifPresent(eventPublisher::publishEvent));
    }
}

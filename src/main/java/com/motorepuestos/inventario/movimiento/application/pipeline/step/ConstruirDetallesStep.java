package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import com.motorepuestos.inventario.movimiento.domain.factory.DetalleMovimientoFactory;
import com.motorepuestos.inventario.movimiento.domain.policy.MovimientoRegistroPolicy;
import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.service.ProductoStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(30)
@RequiredArgsConstructor
public class ConstruirDetallesStep implements MovimientoRegistroStep {

    private final MovimientoRegistroPolicy movimientoRegistroPolicy;
    private final ProductoStockService productoStockService;
    private final DetalleMovimientoFactory detalleMovimientoFactory;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        List<DetalleMovimientoRequest> detallesOrdenados =
                movimientoRegistroPolicy.validarYOrdenarDetalles(context.getCommand().detalles());

        List<DetalleMovimiento> detalles = new ArrayList<>(detallesOrdenados.size());

        for (DetalleMovimientoRequest detalleRequest : detallesOrdenados) {
            Producto producto = productoStockService.obtenerBloqueadoParaMovimiento(
                    detalleRequest.getProductoId()
            );

            CantidadMovimiento cantidad =
                    movimientoRegistroPolicy.validarCantidad(detalleRequest.getCantidad());

            context.getEstrategia().aplicarStock(producto, cantidad.valor());

            detalles.add(detalleMovimientoFactory.crear(producto, cantidad));
        }

        context.setDetalles(detalles);
    }
}

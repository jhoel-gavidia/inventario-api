package com.motorepuestos.inventario.movimiento.application.pipeline.step;

import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroContext;
import com.motorepuestos.inventario.movimiento.application.pipeline.MovimientoRegistroStep;
import com.motorepuestos.inventario.movimiento.application.validation.DetalleMovimientoValidado;
import com.motorepuestos.inventario.movimiento.application.validation.MovimientoValidacionChain;
import com.motorepuestos.inventario.movimiento.domain.factory.DetalleMovimientoFactory;
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

    private final MovimientoValidacionChain movimientoValidacionChain;
    private final ProductoStockService productoStockService;
    private final DetalleMovimientoFactory detalleMovimientoFactory;

    @Override
    public void ejecutar(MovimientoRegistroContext context) {
        List<DetalleMovimientoValidado> detallesValidados =
                movimientoValidacionChain.validar(context.getCommand().detalles());

        List<DetalleMovimiento> detalles = new ArrayList<>(detallesValidados.size());

        for (DetalleMovimientoValidado detalleValidado : detallesValidados) {
            Producto producto = productoStockService.obtenerBloqueadoParaMovimiento(
                    detalleValidado.productoId()
            );

            context.getEstrategia().aplicarStock(
                    producto,
                    detalleValidado.cantidad().valor()
            );

            detalles.add(detalleMovimientoFactory.crear(producto, detalleValidado.cantidad()));
        }

        context.setDetalles(detalles);
    }
}

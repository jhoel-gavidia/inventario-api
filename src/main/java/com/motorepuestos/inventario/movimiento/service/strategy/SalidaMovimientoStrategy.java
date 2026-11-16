package com.motorepuestos.inventario.movimiento.service.strategy;

import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class SalidaMovimientoStrategy implements MovimientoTipoStrategy {

    @Override
    public Tipo tipo() {
        return Tipo.SALIDA;
    }

    @Override
    public void aplicarStock(Producto producto, Integer cantidad) {
        Tipo.SALIDA.aplicar(producto, cantidad);
    }
}

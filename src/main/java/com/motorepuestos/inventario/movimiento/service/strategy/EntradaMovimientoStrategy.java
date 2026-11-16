package com.motorepuestos.inventario.movimiento.service.strategy;

import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class EntradaMovimientoStrategy implements MovimientoTipoStrategy {

    @Override
    public Tipo tipo() {
        return Tipo.ENTRADA;
    }

    @Override
    public void aplicarStock(Producto producto, Integer cantidad) {
        Tipo.ENTRADA.aplicar(producto, cantidad);
    }
}

package com.motorepuestos.inventario.movimiento.service.strategy;

import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.producto.entity.Producto;

public interface MovimientoTipoStrategy {

    Tipo tipo();

    void aplicarStock(Producto producto, Integer cantidad);
}

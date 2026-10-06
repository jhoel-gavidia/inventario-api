package com.motorepuestos.inventario.movimiento.entity;

import com.motorepuestos.inventario.producto.entity.Producto;

public enum Tipo {

    ENTRADA {
        @Override
        public void aplicar(Producto producto, int cantidad) {
            producto.aumentarStock(cantidad);
        }
    },

    SALIDA {
        @Override
        public void aplicar(Producto producto, int cantidad) {
            producto.reducirStock(cantidad);
        }
    };

    public abstract void aplicar(Producto producto, int cantidad);
}
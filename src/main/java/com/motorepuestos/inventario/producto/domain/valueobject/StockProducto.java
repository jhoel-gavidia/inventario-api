package com.motorepuestos.inventario.producto.domain.valueobject;

import com.motorepuestos.inventario.common.exception.BusinessException;

public record StockProducto(int valor) {

    public static final int MAXIMO = 1_000_000_000;

    public StockProducto {
        if (valor < 0) {
            throw new BusinessException("El stock no puede ser negativo");
        }
        if (valor > MAXIMO) {
            throw new BusinessException("El stock no puede superar " + MAXIMO);
        }
    }

    public static StockProducto de(Integer valor) {
        if (valor == null) {
            throw new BusinessException("El stock es obligatorio");
        }
        return new StockProducto(valor);
    }
}

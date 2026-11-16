package com.motorepuestos.inventario.producto.domain.valueobject;

import com.motorepuestos.inventario.common.exception.BusinessException;

import java.math.BigDecimal;

public record PrecioProducto(BigDecimal valor) {

    public PrecioProducto {
        if (valor == null) {
            throw new BusinessException("El precio es obligatorio");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor a 0");
        }
    }

    public static PrecioProducto de(BigDecimal valor) {
        return new PrecioProducto(valor);
    }
}

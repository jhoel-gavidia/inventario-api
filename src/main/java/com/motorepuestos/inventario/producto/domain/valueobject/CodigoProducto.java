package com.motorepuestos.inventario.producto.domain.valueobject;

import com.motorepuestos.inventario.common.exception.BusinessException;

public record CodigoProducto(String valor) {

    public static final int MAX_LONGITUD = 50;

    public CodigoProducto {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("El código es obligatorio");
        }
        valor = valor.trim();
        if (valor.length() > MAX_LONGITUD) {
            throw new BusinessException("El código no puede superar los " + MAX_LONGITUD + " caracteres");
        }
    }

    public static CodigoProducto de(String valor) {
        return new CodigoProducto(valor);
    }
}

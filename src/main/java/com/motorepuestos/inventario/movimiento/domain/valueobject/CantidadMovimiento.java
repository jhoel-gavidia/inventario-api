package com.motorepuestos.inventario.movimiento.domain.valueobject;

import com.motorepuestos.inventario.common.exception.BusinessException;

public record CantidadMovimiento(int valor) {

    public static final int MAXIMA = 1_000_000;

    public CantidadMovimiento {
        if (valor <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }
        if (valor > MAXIMA) {
            throw new BusinessException("La cantidad no puede superar " + MAXIMA);
        }
    }

    public static CantidadMovimiento de(Integer valor) {
        if (valor == null) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }
        return new CantidadMovimiento(valor);
    }
}

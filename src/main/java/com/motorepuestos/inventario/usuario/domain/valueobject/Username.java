package com.motorepuestos.inventario.usuario.domain.valueobject;

import com.motorepuestos.inventario.common.exception.BusinessException;

public record Username(String valor) {

    public static final int MIN_LONGITUD = 4;
    public static final int MAX_LONGITUD = 50;

    public Username {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("El nombre de usuario es obligatorio");
        }
        valor = valor.trim();
        if (valor.length() < MIN_LONGITUD || valor.length() > MAX_LONGITUD) {
            throw new BusinessException(
                    "El username debe tener entre " + MIN_LONGITUD + " y " + MAX_LONGITUD + " caracteres"
            );
        }
    }

    public static Username de(String valor) {
        return new Username(valor);
    }
}

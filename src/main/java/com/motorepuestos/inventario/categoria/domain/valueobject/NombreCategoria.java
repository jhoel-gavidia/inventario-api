package com.motorepuestos.inventario.categoria.domain.valueobject;

import com.motorepuestos.inventario.common.exception.BusinessException;

public record NombreCategoria(String valor) {

    public static final int MAX_LONGITUD = 50;

    public NombreCategoria {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("El nombre de la categoría es obligatorio");
        }
        valor = valor.trim();
        if (valor.length() > MAX_LONGITUD) {
            throw new BusinessException("El nombre no puede superar los " + MAX_LONGITUD + " caracteres");
        }
    }

    public static NombreCategoria de(String valor) {
        return new NombreCategoria(valor);
    }
}

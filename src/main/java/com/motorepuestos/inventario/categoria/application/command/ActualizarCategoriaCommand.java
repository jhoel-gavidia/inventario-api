package com.motorepuestos.inventario.categoria.application.command;

import com.motorepuestos.inventario.categoria.dto.CategoriaRequest;

public record ActualizarCategoriaCommand(Long id, CategoriaRequest request) {
}

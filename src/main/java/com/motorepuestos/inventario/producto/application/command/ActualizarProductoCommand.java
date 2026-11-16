package com.motorepuestos.inventario.producto.application.command;

import com.motorepuestos.inventario.producto.dto.ProductoUpdateRequest;

public record ActualizarProductoCommand(Long id, ProductoUpdateRequest request) {
}

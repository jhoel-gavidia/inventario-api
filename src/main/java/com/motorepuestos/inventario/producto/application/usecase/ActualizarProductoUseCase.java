package com.motorepuestos.inventario.producto.application.usecase;

import com.motorepuestos.inventario.producto.application.command.ActualizarProductoCommand;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;

public interface ActualizarProductoUseCase {
    ProductoResponse ejecutar(ActualizarProductoCommand command);
}

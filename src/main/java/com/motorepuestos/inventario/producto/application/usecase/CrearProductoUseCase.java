package com.motorepuestos.inventario.producto.application.usecase;

import com.motorepuestos.inventario.producto.application.command.CrearProductoCommand;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;

public interface CrearProductoUseCase {
    ProductoResponse ejecutar(CrearProductoCommand command);
}

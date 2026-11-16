package com.motorepuestos.inventario.producto.application.usecase;

import com.motorepuestos.inventario.producto.application.command.EliminarProductoCommand;

public interface EliminarProductoUseCase {
    void ejecutar(EliminarProductoCommand command);
}

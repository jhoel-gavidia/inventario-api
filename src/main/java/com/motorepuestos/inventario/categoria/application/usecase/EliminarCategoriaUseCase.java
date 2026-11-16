package com.motorepuestos.inventario.categoria.application.usecase;

import com.motorepuestos.inventario.categoria.application.command.EliminarCategoriaCommand;

public interface EliminarCategoriaUseCase {
    void ejecutar(EliminarCategoriaCommand command);
}

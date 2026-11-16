package com.motorepuestos.inventario.categoria.application.usecase;

import com.motorepuestos.inventario.categoria.application.command.ActualizarCategoriaCommand;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;

public interface ActualizarCategoriaUseCase {
    CategoriaResponse ejecutar(ActualizarCategoriaCommand command);
}

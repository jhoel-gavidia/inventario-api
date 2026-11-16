package com.motorepuestos.inventario.categoria.application.usecase;

import com.motorepuestos.inventario.categoria.application.command.CrearCategoriaCommand;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;

public interface CrearCategoriaUseCase {
    CategoriaResponse ejecutar(CrearCategoriaCommand command);
}

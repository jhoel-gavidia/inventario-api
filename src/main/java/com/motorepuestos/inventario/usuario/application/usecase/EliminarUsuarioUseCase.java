package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.usuario.application.command.EliminarUsuarioCommand;

public interface EliminarUsuarioUseCase {
    void ejecutar(EliminarUsuarioCommand command);
}

package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.usuario.application.command.ActualizarUsuarioCommand;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;

public interface ActualizarUsuarioUseCase {
    UsuarioResponse ejecutar(ActualizarUsuarioCommand command);
}

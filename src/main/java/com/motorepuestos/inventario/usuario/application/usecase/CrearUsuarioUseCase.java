package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.usuario.application.command.CrearUsuarioCommand;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;

public interface CrearUsuarioUseCase {
    UsuarioResponse ejecutar(CrearUsuarioCommand command);
}

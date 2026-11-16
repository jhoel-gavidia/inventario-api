package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.usuario.application.command.CambiarPasswordCommand;

public interface CambiarPasswordUseCase {
    void ejecutar(CambiarPasswordCommand command);
}

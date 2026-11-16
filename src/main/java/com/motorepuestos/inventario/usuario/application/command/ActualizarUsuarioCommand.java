package com.motorepuestos.inventario.usuario.application.command;

import com.motorepuestos.inventario.usuario.dto.UsuarioUpdateRequest;

public record ActualizarUsuarioCommand(Long id, UsuarioUpdateRequest request) {
}

package com.motorepuestos.inventario.usuario.service.query;

import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioQueryService {
    UsuarioResponse obtenerPorId(Long id);

    List<UsuarioResponse> obtenerTodos();

    UsuarioResponse obtenerPorUsername(String username);
}

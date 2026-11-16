package com.motorepuestos.inventario.usuario.service.query;

import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;

import java.util.List;

import com.motorepuestos.inventario.usuario.entity.Rol;

public interface UsuarioQueryService {
    UsuarioResponse obtenerPorId(Long id);

    List<UsuarioResponse> obtenerTodos(String search, Rol rol, Boolean estado);

    UsuarioResponse obtenerPorUsername(String username);
}

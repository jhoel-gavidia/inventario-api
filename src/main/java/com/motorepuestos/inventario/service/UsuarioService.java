package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.UsuarioRequest;
import com.motorepuestos.inventario.DTOs.Request.UsuarioUpdateRequest;
import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse crear(UsuarioRequest request);

    UsuarioResponse obtenerPorId(Long id);

    List<UsuarioResponse> obtenerTodos();

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    void eliminar(Long id);
}

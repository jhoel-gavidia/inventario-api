package com.motorepuestos.inventario.usuario.service;

import com.motorepuestos.inventario.usuario.dto.CambiarPasswordRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioUpdateRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;

import com.motorepuestos.inventario.usuario.entity.Rol;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse crear(UsuarioRequest request);

    UsuarioResponse obtenerPorId(Long id);

    List<UsuarioResponse> obtenerTodos(String search, Rol rol, Boolean estado);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    UsuarioResponse obtenerPorUsername(String username);

    void eliminar(Long id);

    void cambiarPassword(CambiarPasswordRequest request);

}

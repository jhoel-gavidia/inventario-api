package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.CategoriaRequest;
import com.motorepuestos.inventario.DTOs.Response.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse obtenerPorId(Long id);

    List<CategoriaResponse> obtenerTodos();

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void eliminar(Long id);
}

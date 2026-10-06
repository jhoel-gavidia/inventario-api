package com.motorepuestos.inventario.categoria.service;

import com.motorepuestos.inventario.categoria.dto.CategoriaRequest;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse obtenerPorId(Long id);

    List<CategoriaResponse> obtenerTodos();

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void eliminar(Long id);
}

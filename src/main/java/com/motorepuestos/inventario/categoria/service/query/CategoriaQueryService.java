package com.motorepuestos.inventario.categoria.service.query;

import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaQueryService {

    CategoriaResponse obtenerPorId(Long id);

    List<CategoriaResponse> obtenerTodos(String search, Boolean estado);
}

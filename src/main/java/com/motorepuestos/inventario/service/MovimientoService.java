package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;

import java.util.List;

public interface MovimientoService {
    MovimientoResponse registrar(MovimientoRequest request);

    MovimientoResponse obtenerPorId(Long id);

    List<MovimientoResponse> obtenerTodos();
}

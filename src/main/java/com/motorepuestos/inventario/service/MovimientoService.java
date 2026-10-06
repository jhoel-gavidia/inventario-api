package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.DTOs.Response.PaginaResponse;
import org.springframework.data.domain.Pageable;

public interface MovimientoService {
    MovimientoResponse registrar(MovimientoRequest request);

    MovimientoResponse obtenerPorId(Long id);

    PaginaResponse<MovimientoResponse> obtenerTodos(Pageable pageable);
}
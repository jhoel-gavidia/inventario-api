package com.motorepuestos.inventario.movimiento.service;

import com.motorepuestos.inventario.movimiento.dto.MovimientoRequest;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.common.dto.PaginaResponse;
import org.springframework.data.domain.Pageable;

public interface MovimientoService {
    MovimientoResponse registrar(MovimientoRequest request);

    MovimientoResponse obtenerPorId(Long id);

    PaginaResponse<MovimientoResponse> obtenerTodos(Pageable pageable);
}
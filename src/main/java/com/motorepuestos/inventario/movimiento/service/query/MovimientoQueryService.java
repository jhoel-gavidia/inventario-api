package com.motorepuestos.inventario.movimiento.service.query;

import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResumenResponse;
import org.springframework.data.domain.Pageable;

public interface MovimientoQueryService {

    MovimientoResponse obtenerPorId(Long id);

    PaginaResponse<MovimientoResponse> obtenerTodos(Pageable pageable);

    PaginaResponse<MovimientoDetalleView> buscarDetalles(String tipo, String search, Pageable pageable);

    MovimientoResumenResponse resumenHoy();
}

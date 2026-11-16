package com.motorepuestos.inventario.movimiento.application.query;

import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.mapper.MovimientoMapper;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListarMovimientosQueryHandler {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;

    public PaginaResponse<MovimientoResponse> handle(ListarMovimientosQuery query) {
        return PaginaResponse.from(
                movimientoRepository.findAllBy(query.pageable()),
                movimientoMapper::toResponse
        );
    }
}

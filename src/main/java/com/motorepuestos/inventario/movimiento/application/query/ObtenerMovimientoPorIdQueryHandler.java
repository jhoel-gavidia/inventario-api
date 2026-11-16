package com.motorepuestos.inventario.movimiento.application.query;

import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.mapper.MovimientoMapper;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObtenerMovimientoPorIdQueryHandler {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;

    public MovimientoResponse handle(ObtenerMovimientoPorIdQuery query) {
        Movimiento movimiento = movimientoRepository.findById(query.id())
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + query.id()));
        return movimientoMapper.toResponse(movimiento);
    }
}

package com.motorepuestos.inventario.movimiento.mapper;

import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = DetalleMovimientoMapper.class,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MovimientoMapper {

    MovimientoResponse toResponse(Movimiento movimiento);
}

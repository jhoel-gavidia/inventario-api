package com.motorepuestos.inventario.mapper;

import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.entity.Movimiento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = DetalleMovimientoMapper.class)
public interface MovimientoMapper {

    Movimiento toEntity(MovimientoRequest request);

    MovimientoResponse toResponse(Movimiento movimiento);
}

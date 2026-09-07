package com.motorepuestos.inventario.mapper;

import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.entity.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = DetalleMovimientoMapper.class)
public interface MovimientoMapper {

    MovimientoResponse toResponse(Movimiento movimiento);
}

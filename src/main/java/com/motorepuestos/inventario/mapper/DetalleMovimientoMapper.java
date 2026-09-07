package com.motorepuestos.inventario.mapper;


import com.motorepuestos.inventario.DTOs.Request.DetalleMovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.DetalleMovimientoResponse;
import com.motorepuestos.inventario.entity.DetalleMovimiento;

public interface DetalleMovimientoMapper {
    DetalleMovimiento toEntity(DetalleMovimientoRequest request);
    DetalleMovimientoResponse toResponse(DetalleMovimiento detalleMovimiento);
}

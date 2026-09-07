package com.motorepuestos.inventario.mapper;


import com.motorepuestos.inventario.DTOs.Request.DetalleMovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.DetalleMovimientoResponse;
import com.motorepuestos.inventario.entity.DetalleMovimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetalleMovimientoMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    DetalleMovimientoResponse toResponse(DetalleMovimiento detalleMovimiento);
}

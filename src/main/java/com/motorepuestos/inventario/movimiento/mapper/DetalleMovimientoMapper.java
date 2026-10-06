package com.motorepuestos.inventario.movimiento.mapper;


import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoResponse;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface DetalleMovimientoMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    DetalleMovimientoResponse toResponse(DetalleMovimiento detalleMovimiento);
}

package com.motorepuestos.inventario.mapper;

import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;
import com.motorepuestos.inventario.entity.Auditoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditoriaMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.username", target = "username")
    AuditoriaResponse toResponse(Auditoria auditoria);
}

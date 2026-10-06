package com.motorepuestos.inventario.auditoria.mapper;

import com.motorepuestos.inventario.auditoria.dto.AuditoriaResponse;
import com.motorepuestos.inventario.auditoria.entity.Auditoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AuditoriaMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.username", target = "username")
    AuditoriaResponse toResponse(Auditoria auditoria);
}

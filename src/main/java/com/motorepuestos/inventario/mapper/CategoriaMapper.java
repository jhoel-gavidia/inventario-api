package com.motorepuestos.inventario.mapper;

import com.motorepuestos.inventario.DTOs.Request.CategoriaRequest;
import com.motorepuestos.inventario.DTOs.Response.CategoriaResponse;
import com.motorepuestos.inventario.entity.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequest request);

    CategoriaResponse toResponse(Categoria categoria);
}

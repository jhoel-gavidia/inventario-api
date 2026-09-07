package com.motorepuestos.inventario.mapper;

import com.motorepuestos.inventario.DTOs.Request.ProductoRequest;
import com.motorepuestos.inventario.DTOs.Response.ProductoResponse;
import com.motorepuestos.inventario.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(source = "stockInicial", target = "stockActual")
    @Mapping(target = "categoria", ignore = true)
    Producto toEntity(ProductoRequest request);

    @Mapping(source = "categoria.id", target = "categoriaId")
    ProductoResponse toResponse(Producto producto);
}

package com.motorepuestos.inventario.producto.mapper;

import com.motorepuestos.inventario.producto.dto.ProductoRequest;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProductoMapper {
    @Mapping(source = "stockInicial", target = "stockActual")
    @Mapping(target = "categoria", ignore = true)
    Producto toEntity(ProductoRequest request);

    @Mapping(source = "categoria.id", target = "categoriaId")
    ProductoResponse toResponse(Producto producto);
}

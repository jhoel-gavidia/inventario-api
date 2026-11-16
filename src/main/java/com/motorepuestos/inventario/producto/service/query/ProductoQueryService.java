package com.motorepuestos.inventario.producto.service.query;

import com.motorepuestos.inventario.producto.dto.ProductoResponse;

import java.util.List;

public interface ProductoQueryService {
    ProductoResponse obtenerPorId(Long id);

    List<ProductoResponse> obtenerTodos();
}

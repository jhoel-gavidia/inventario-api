package com.motorepuestos.inventario.producto.service;

import com.motorepuestos.inventario.producto.dto.ProductoRequest;
import com.motorepuestos.inventario.producto.dto.ProductoUpdateRequest;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {
    ProductoResponse crear(ProductoRequest request);

    ProductoResponse obtenerPorId(Long id);

    List<ProductoResponse> obtenerTodos();

    ProductoResponse actualizar(Long id, ProductoUpdateRequest request);

    void eliminar(Long id);
}

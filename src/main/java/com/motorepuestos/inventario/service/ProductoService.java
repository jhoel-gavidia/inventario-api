package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.ProductoRequest;
import com.motorepuestos.inventario.DTOs.Request.ProductoUpdateRequest;
import com.motorepuestos.inventario.DTOs.Response.ProductoResponse;

import java.util.List;

public interface ProductoService {
    ProductoResponse crear(ProductoRequest request);

    ProductoResponse obtenerPorId(Long id);

    List<ProductoResponse> obtenerTodos();

    ProductoResponse actualizar(Long id, ProductoUpdateRequest request);

    void eliminar(Long id);
}

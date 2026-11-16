package com.motorepuestos.inventario.producto.service;

import com.motorepuestos.inventario.producto.application.command.ActualizarProductoCommand;
import com.motorepuestos.inventario.producto.application.command.CrearProductoCommand;
import com.motorepuestos.inventario.producto.application.command.EliminarProductoCommand;
import com.motorepuestos.inventario.producto.application.usecase.ActualizarProductoUseCase;
import com.motorepuestos.inventario.producto.application.usecase.CrearProductoUseCase;
import com.motorepuestos.inventario.producto.application.usecase.EliminarProductoUseCase;
import com.motorepuestos.inventario.producto.dto.ProductoRequest;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.producto.dto.ProductoUpdateRequest;
import com.motorepuestos.inventario.producto.service.query.ProductoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductoServiceImpl implements ProductoService {

    private final CrearProductoUseCase crearProductoUseCase;
    private final ActualizarProductoUseCase actualizarProductoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;
    private final ProductoQueryService productoQueryService;

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        return crearProductoUseCase.ejecutar(new CrearProductoCommand(request));
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        return productoQueryService.obtenerPorId(id);
    }

    @Override
    public List<ProductoResponse> obtenerTodos() {
        return productoQueryService.obtenerTodos();
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoUpdateRequest request) {
        return actualizarProductoUseCase.ejecutar(new ActualizarProductoCommand(id, request));
    }

    @Override
    public void eliminar(Long id) {
        eliminarProductoUseCase.ejecutar(new EliminarProductoCommand(id));
    }
}
package com.motorepuestos.inventario.producto.service;

import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.application.command.ActualizarProductoCommand;
import com.motorepuestos.inventario.producto.application.command.CrearProductoCommand;
import com.motorepuestos.inventario.producto.application.command.EliminarProductoCommand;
import com.motorepuestos.inventario.producto.application.usecase.ActualizarProductoUseCase;
import com.motorepuestos.inventario.producto.application.usecase.CrearProductoUseCase;
import com.motorepuestos.inventario.producto.application.usecase.EliminarProductoUseCase;
import com.motorepuestos.inventario.producto.dto.ProductoRequest;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.producto.dto.ProductoUpdateRequest;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.mapper.ProductoMapper;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final CrearProductoUseCase crearProductoUseCase;
    private final ActualizarProductoUseCase actualizarProductoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        return crearProductoUseCase.ejecutar(new CrearProductoCommand(request));
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.toResponse(obtenerProductoSinLockOrThrow(id));
    }

    @Override
    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findByEstadoTrue()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoUpdateRequest request) {
        return actualizarProductoUseCase.ejecutar(new ActualizarProductoCommand(id, request));
    }

    @Override
    public void eliminar(Long id) {
        eliminarProductoUseCase.ejecutar(new EliminarProductoCommand(id));
    }

    private Producto obtenerProductoSinLockOrThrow(Long id) {
        return productoRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
}
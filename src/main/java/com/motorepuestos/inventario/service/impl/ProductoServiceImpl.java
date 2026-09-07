package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.ProductoRequest;
import com.motorepuestos.inventario.DTOs.Response.ProductoResponse;
import com.motorepuestos.inventario.entity.Categoria;
import com.motorepuestos.inventario.entity.Producto;
import com.motorepuestos.inventario.mapper.ProductoMapper;
import com.motorepuestos.inventario.repository.CategoriaRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId()).orElseThrow(
                () -> new RuntimeException("Categoria no encontrada")
        );

        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);

        productoRepository.save(producto);

        return productoMapper.toResponse(producto);
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Producto no encontrado")
        );

        return productoMapper.toResponse(producto);
    }

    @Override
    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Producto no encontrado")
        );

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId()).orElseThrow(
                () -> new RuntimeException("Categoria no encontrada")
        );

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setCategoria(categoria);
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setEstado(request.getEstado());

        productoRepository.save(producto);

        return productoMapper.toResponse(producto);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Producto no encontrado")
        );
        productoRepository.delete(producto);
    }
}

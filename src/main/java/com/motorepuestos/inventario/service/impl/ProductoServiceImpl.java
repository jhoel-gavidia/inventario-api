package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.ProductoRequest;
import com.motorepuestos.inventario.DTOs.Response.ProductoResponse;
import com.motorepuestos.inventario.entity.Categoria;
import com.motorepuestos.inventario.entity.Producto;
import com.motorepuestos.inventario.exception.ResourceConflictException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.ProductoMapper;
import com.motorepuestos.inventario.repository.CategoriaRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.service.ProductoService;
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
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        validarCodigoDisponible(request.getCodigo());
        Categoria categoria = obtenerCategoriaOrThrow(request.getCategoriaId());

        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);

        productoRepository.save(producto);

        return productoMapper.toResponse(producto);
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.toResponse(obtenerProductoOrThrow(id));
    }

    @Override
    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerProductoOrThrow(id);

        boolean cambioCodigo = !producto.getCodigo().equals(request.getCodigo());

        if (cambioCodigo) {
            validarCodigoDisponible(request.getCodigo());
        }

        Categoria categoria = obtenerCategoriaOrThrow(request.getCategoriaId());

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setCategoria(categoria);
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setEstado(request.getEstado());


        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = obtenerProductoOrThrow(id);
        producto.setEstado(false);
    }

    private Producto obtenerProductoOrThrow(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }


    private Categoria obtenerCategoriaOrThrow(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + categoriaId));
    }

    private void validarCodigoDisponible(String codigo) {
        if (productoRepository.existsByCodigo(codigo)) {
            throw new ResourceConflictException("El código del producto ya existe: " + codigo);
        }
    }
}

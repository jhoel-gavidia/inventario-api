package com.motorepuestos.inventario.producto.service.query;

import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.mapper.ProductoMapper;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoQueryServiceImpl implements ProductoQueryService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

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

    private Producto obtenerProductoSinLockOrThrow(Long id) {
        return productoRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
}

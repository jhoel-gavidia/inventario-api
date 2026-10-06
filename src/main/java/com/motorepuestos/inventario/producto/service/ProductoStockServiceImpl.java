package com.motorepuestos.inventario.producto.service;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoStockServiceImpl implements ProductoStockService {

    private final ProductoRepository productoRepository;

    @Override
    public Producto obtenerBloqueadoParaMovimiento(Long id) {
        Producto producto = productoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw new BusinessException(
                    "El producto está inactivo y no admite movimientos: " + producto.getCodigo()
            );
        }

        return producto;
    }
}

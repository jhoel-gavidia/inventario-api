package com.motorepuestos.inventario.producto.service;

import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoConsultaServiceImpl implements ProductoConsultaService {

    private final ProductoRepository productoRepository;

    @Override
    public boolean existenActivosPorCategoria(Long categoriaId) {
        return productoRepository.existsByCategoriaIdAndEstadoTrue(categoriaId);
    }
}
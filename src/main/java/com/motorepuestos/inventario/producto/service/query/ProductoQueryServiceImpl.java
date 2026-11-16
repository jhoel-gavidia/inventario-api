package com.motorepuestos.inventario.producto.service.query;

import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.mapper.ProductoMapper;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import com.motorepuestos.inventario.producto.repository.ProductoSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    public List<ProductoResponse> obtenerTodos(String search, Boolean estado, Long categoriaId, Integer stockMaximo) {
        Boolean estadoFiltro = estado != null ? estado : Boolean.TRUE;

        Specification<Producto> specification = Specification
                .where(ProductoSpecifications.conBusqueda(search))
                .and(ProductoSpecifications.conEstado(estadoFiltro))
                .and(ProductoSpecifications.conCategoriaId(categoriaId))
                .and(ProductoSpecifications.conStockMaximo(stockMaximo));

        return productoRepository.findAll(specification, Sort.by(Sort.Direction.ASC, "nombre"))
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    private Producto obtenerProductoSinLockOrThrow(Long id) {
        return productoRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
}

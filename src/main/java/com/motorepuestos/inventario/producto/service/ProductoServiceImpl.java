package com.motorepuestos.inventario.producto.service;

import com.motorepuestos.inventario.producto.dto.ProductoRequest;
import com.motorepuestos.inventario.producto.dto.ProductoUpdateRequest;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.mapper.ProductoMapper;
import com.motorepuestos.inventario.categoria.service.CategoriaConsultaService;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
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
    private final CategoriaConsultaService categoriaConsultaService;
    private final AuditoriaHelper auditoriaHelper;

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        validarCodigoDisponible(request.getCodigo());
        Categoria categoria = categoriaConsultaService.obtenerActiva(request.getCategoriaId());

        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);

        productoRepository.save(producto);

        ProductoResponse response = productoMapper.toResponse(producto);

        auditoriaHelper.registrarCreacion("CREAR", "PRODUCTO", producto.getId(), response);

        return response;
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        // Solo lectura: sin lock, para no romper la transacción readOnly
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
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoUpdateRequest request) {

        Producto producto = obtenerProductoConLockOrThrow(id);

        boolean cambioCodigo = !producto.getCodigo().equals(request.getCodigo());

        if (cambioCodigo) {
            validarCodigoDisponible(request.getCodigo());
        }

        Categoria categoria = categoriaConsultaService.obtenerActiva(request.getCategoriaId());

        auditoriaHelper.registrarCambio(
                "ACTUALIZAR",
                "PRODUCTO",
                id,
                producto,
                productoMapper::toResponse,
                () -> {
                    producto.setCodigo(request.getCodigo());
                    producto.setNombre(request.getNombre());
                    producto.setCategoria(categoria);
                    producto.setPrecioCompra(request.getPrecioCompra());
                    producto.setPrecioVenta(request.getPrecioVenta());
                    producto.setEstado(request.getEstado());
                }
        );

        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        // Escritura (soft delete): con lock
        Producto producto = obtenerProductoConLockOrThrow(id);

        auditoriaHelper.registrarCambio(
                "ELIMINAR",
                "PRODUCTO",
                id,
                producto,
                productoMapper::toResponse,
                () -> producto.setEstado(false)
        );
    }

    private Producto obtenerProductoSinLockOrThrow(Long id) {
        return productoRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    private Producto obtenerProductoConLockOrThrow(Long id) {
        return productoRepository.findByIdForUpdate(id)
                .filter(producto -> Boolean.TRUE.equals(producto.getEstado()))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    private void validarCodigoDisponible(String codigo) {
        if (productoRepository.existsByCodigoAndEstadoTrue(codigo)) {
            throw new ResourceConflictException("El código del producto ya existe: " + codigo);
        }
    }
}
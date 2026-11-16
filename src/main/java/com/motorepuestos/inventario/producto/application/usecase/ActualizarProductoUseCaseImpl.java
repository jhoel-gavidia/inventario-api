package com.motorepuestos.inventario.producto.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.service.CategoriaConsultaService;
import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.application.command.ActualizarProductoCommand;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.producto.dto.ProductoUpdateRequest;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.mapper.ProductoMapper;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarProductoUseCaseImpl implements ActualizarProductoUseCase {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final CategoriaConsultaService categoriaConsultaService;
    private final AuditoriaHelper auditoriaHelper;

    @Override
    @Transactional
    public ProductoResponse ejecutar(ActualizarProductoCommand command) {
        ProductoUpdateRequest request = command.request();
        Producto producto = obtenerProductoConLockOrThrow(command.id());

        boolean cambioCodigo = !producto.getCodigo().equals(request.getCodigo());
        if (cambioCodigo) {
            validarCodigoDisponible(request.getCodigo());
        }

        Categoria categoria = categoriaConsultaService.obtenerActiva(request.getCategoriaId());

        auditoriaHelper.registrarCambio(
                "ACTUALIZAR",
                "PRODUCTO",
                command.id(),
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

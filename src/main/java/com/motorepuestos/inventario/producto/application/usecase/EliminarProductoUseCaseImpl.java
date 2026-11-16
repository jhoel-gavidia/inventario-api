package com.motorepuestos.inventario.producto.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.application.command.EliminarProductoCommand;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.mapper.ProductoMapper;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarProductoUseCaseImpl implements EliminarProductoUseCase {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final AuditoriaHelper auditoriaHelper;

    @Override
    @Transactional
    public void ejecutar(EliminarProductoCommand command) {
        Producto producto = obtenerProductoConLockOrThrow(command.id());

        auditoriaHelper.registrarCambio(
                "ELIMINAR",
                "PRODUCTO",
                command.id(),
                producto,
                productoMapper::toResponse,
                () -> producto.setEstado(false)
        );
    }

    private Producto obtenerProductoConLockOrThrow(Long id) {
        return productoRepository.findByIdForUpdate(id)
                .filter(producto -> Boolean.TRUE.equals(producto.getEstado()))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
}

package com.motorepuestos.inventario.producto.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.service.CategoriaConsultaService;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.producto.application.command.ActualizarProductoCommand;
import com.motorepuestos.inventario.producto.domain.policy.ProductoCodigoPolicy;
import com.motorepuestos.inventario.producto.domain.valueobject.CodigoProducto;
import com.motorepuestos.inventario.producto.domain.valueobject.PrecioProducto;
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
    private final ProductoCodigoPolicy productoCodigoPolicy;

    @Override
    @Transactional
    public ProductoResponse ejecutar(ActualizarProductoCommand command) {
        ProductoUpdateRequest request = command.request();
        Producto producto = obtenerProductoConLockOrThrow(command.id());

        CodigoProducto codigo = productoCodigoPolicy.validarYNormalizar(request.getCodigo());
        PrecioProducto precioCompra = PrecioProducto.de(request.getPrecioCompra());
        PrecioProducto precioVenta = PrecioProducto.de(request.getPrecioVenta());

        productoCodigoPolicy.validarDisponibleParaActualizacion(producto, codigo);
        Categoria categoria = categoriaConsultaService.obtenerActiva(request.getCategoriaId());

        auditoriaHelper.registrarCambio(
                "ACTUALIZAR",
                "PRODUCTO",
                command.id(),
                producto,
                productoMapper::toResponse,
                () -> {
                    producto.setCodigo(codigo.valor());
                    producto.setNombre(request.getNombre());
                    producto.setCategoria(categoria);
                    producto.setPrecioCompra(precioCompra.valor());
                    producto.setPrecioVenta(precioVenta.valor());
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
}

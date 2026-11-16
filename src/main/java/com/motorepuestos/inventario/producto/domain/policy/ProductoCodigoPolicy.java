package com.motorepuestos.inventario.producto.domain.policy;

import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import com.motorepuestos.inventario.producto.domain.valueobject.CodigoProducto;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoCodigoPolicy {

    private final ProductoRepository productoRepository;

    public CodigoProducto validarYNormalizar(String codigo) {
        return CodigoProducto.de(codigo);
    }

    public void validarDisponibleParaCreacion(CodigoProducto codigo) {
        if (productoRepository.existsByCodigoAndEstadoTrue(codigo.valor())) {
            throw new ResourceConflictException("El código del producto ya existe: " + codigo.valor());
        }
    }

    public void validarDisponibleParaActualizacion(Producto producto, CodigoProducto nuevoCodigo) {
        boolean cambioCodigo = !producto.getCodigo().equals(nuevoCodigo.valor());
        if (cambioCodigo && productoRepository.existsByCodigoAndEstadoTrue(nuevoCodigo.valor())) {
            throw new ResourceConflictException("El código del producto ya existe: " + nuevoCodigo.valor());
        }
    }
}

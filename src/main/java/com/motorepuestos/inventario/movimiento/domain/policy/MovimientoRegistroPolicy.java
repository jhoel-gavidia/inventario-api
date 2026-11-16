package com.motorepuestos.inventario.movimiento.domain.policy;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MovimientoRegistroPolicy {

    public List<DetalleMovimientoRequest> validarYOrdenarDetalles(List<DetalleMovimientoRequest> detalles) {
        validarProductosNoRepetidos(detalles);
        return detalles.stream()
                .sorted(Comparator.comparing(DetalleMovimientoRequest::getProductoId))
                .toList();
    }

    public CantidadMovimiento validarCantidad(Integer cantidad) {
        return CantidadMovimiento.de(cantidad);
    }

    private void validarProductosNoRepetidos(List<DetalleMovimientoRequest> detalles) {
        Set<Long> productos = new HashSet<>();
        for (DetalleMovimientoRequest detalle : detalles) {
            if (detalle.getProductoId() == null) {
                throw new BusinessException("El id del producto es obligatorio");
            }
            if (!productos.add(detalle.getProductoId())) {
                throw new BusinessException("El producto no puede repetirse dentro del mismo movimiento");
            }
        }
    }
}

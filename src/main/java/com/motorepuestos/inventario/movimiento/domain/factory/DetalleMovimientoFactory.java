package com.motorepuestos.inventario.movimiento.domain.factory;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class DetalleMovimientoFactory {

    public DetalleMovimiento crear(Producto producto, CantidadMovimiento cantidad) {
        if (producto == null) {
            throw new BusinessException("El producto es obligatorio");
        }
        if (cantidad == null) {
            throw new BusinessException("La cantidad es obligatoria");
        }

        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad.valor());
        return detalle;
    }
}

package com.motorepuestos.inventario.producto.domain.policy;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMovimientoPolicy {

    public void validarAptoParaMovimiento(Producto producto) {
        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw new BusinessException(
                    "El producto está inactivo y no admite movimientos: " + producto.getCodigo()
            );
        }
    }
}

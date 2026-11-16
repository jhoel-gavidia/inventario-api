package com.motorepuestos.inventario.movimiento.domain.factory;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DetalleMovimientoFactoryTest {

    private final DetalleMovimientoFactory detalleMovimientoFactory = new DetalleMovimientoFactory();

    @Test
    void crear_debeAsociarProductoYCantidad() {
        Producto producto = new Producto();
        producto.setCodigo("FIL-001");

        DetalleMovimiento detalle = detalleMovimientoFactory.crear(
                producto,
                CantidadMovimiento.de(5)
        );

        assertThat(detalle.getProducto()).isSameAs(producto);
        assertThat(detalle.getCantidad()).isEqualTo(5);
    }

    @Test
    void crear_sinProducto_debeLanzarExcepcion() {
        assertThatThrownBy(() -> detalleMovimientoFactory.crear(
                null,
                CantidadMovimiento.de(5)
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("producto");
    }

    @Test
    void crear_sinCantidad_debeLanzarExcepcion() {
        assertThatThrownBy(() -> detalleMovimientoFactory.crear(
                new Producto(),
                null
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cantidad");
    }
}

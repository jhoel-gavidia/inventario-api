package com.motorepuestos.inventario.movimiento.domain.policy;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovimientoRegistroPolicyTest {

    private final MovimientoRegistroPolicy movimientoRegistroPolicy = new MovimientoRegistroPolicy();

    @Test
    void validarYOrdenarDetalles_debeOrdenarPorProductoId() {
        DetalleMovimientoRequest detalle2 = detalle(2L, 3);
        DetalleMovimientoRequest detalle1 = detalle(1L, 5);

        List<DetalleMovimientoRequest> resultado =
                movimientoRegistroPolicy.validarYOrdenarDetalles(List.of(detalle2, detalle1));

        assertThat(resultado).containsExactly(detalle1, detalle2);
    }

    @Test
    void validarYOrdenarDetalles_conListaVacia_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoRegistroPolicy.validarYOrdenarDetalles(List.of()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("al menos un detalle");
    }

    @Test
    void validarYOrdenarDetalles_conListaNula_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoRegistroPolicy.validarYOrdenarDetalles(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("al menos un detalle");
    }

    @Test
    void validarYOrdenarDetalles_conProductoRepetido_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoRegistroPolicy.validarYOrdenarDetalles(
                List.of(detalle(1L, 2), detalle(1L, 3))
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no puede repetirse");
    }

    @Test
    void validarYOrdenarDetalles_sinProductoId_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoRegistroPolicy.validarYOrdenarDetalles(
                List.of(detalle(null, 2))
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("id del producto");
    }

    @Test
    void validarCantidad_nula_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoRegistroPolicy.validarCantidad(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mayor que cero");
    }

    private DetalleMovimientoRequest detalle(Long productoId, Integer cantidad) {
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(productoId);
        detalle.setCantidad(cantidad);
        return detalle;
    }
}

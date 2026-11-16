package com.motorepuestos.inventario.movimiento.application.validation;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.application.validation.handler.CantidadPositivaHandler;
import com.motorepuestos.inventario.movimiento.application.validation.handler.DetallesNoVaciosHandler;
import com.motorepuestos.inventario.movimiento.application.validation.handler.OrdenarDetallesHandler;
import com.motorepuestos.inventario.movimiento.application.validation.handler.ProductoIdObligatorioHandler;
import com.motorepuestos.inventario.movimiento.application.validation.handler.ProductosNoRepetidosHandler;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovimientoValidacionChainTest {

    private final MovimientoValidacionChain chain = new MovimientoValidacionChain(List.of(
            new DetallesNoVaciosHandler(),
            new ProductoIdObligatorioHandler(),
            new ProductosNoRepetidosHandler(),
            new CantidadPositivaHandler(),
            new OrdenarDetallesHandler()
    ));

    @Test
    void validar_debeOrdenarLosDetallesPorProductoId() {
        List<DetalleMovimientoValidado> resultado = chain.validar(List.of(
                detalle(2L, 3),
                detalle(1L, 5)
        ));

        assertThat(resultado)
                .extracting(DetalleMovimientoValidado::productoId)
                .containsExactly(1L, 2L);
        assertThat(resultado)
                .extracting(detalle -> detalle.cantidad().valor())
                .containsExactly(5, 3);
    }

    @Test
    void validar_conListaVacia_debeLanzarExcepcion() {
        assertThatThrownBy(() -> chain.validar(List.of()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("al menos un detalle");
    }

    @Test
    void validar_conListaNula_debeLanzarExcepcion() {
        assertThatThrownBy(() -> chain.validar(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("al menos un detalle");
    }

    @Test
    void validar_sinProductoId_debeLanzarExcepcion() {
        assertThatThrownBy(() -> chain.validar(List.of(detalle(null, 2))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("id del producto");
    }

    @Test
    void validar_conProductoRepetido_debeLanzarExcepcion() {
        assertThatThrownBy(() -> chain.validar(List.of(
                detalle(1L, 2),
                detalle(1L, 3)
        )))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no puede repetirse");
    }

    @Test
    void validar_conCantidadCero_debeLanzarExcepcion() {
        assertThatThrownBy(() -> chain.validar(List.of(detalle(1L, 0))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mayor que cero");
    }

    @Test
    void validar_conCantidadNula_debeLanzarExcepcion() {
        assertThatThrownBy(() -> chain.validar(List.of(detalle(1L, null))))
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

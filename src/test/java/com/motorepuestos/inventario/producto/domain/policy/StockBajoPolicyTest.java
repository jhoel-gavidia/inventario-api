package com.motorepuestos.inventario.producto.domain.policy;

import com.motorepuestos.inventario.producto.domain.event.StockBajoEvent;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockBajoPolicyTest {

    private final StockBajoPolicy stockBajoPolicy = new StockBajoPolicy(5);

    @Test
    void evaluar_cuandoElStockEsIgualAlMinimo_debeEmitirEvento() {
        Producto producto = productoConStock(5);

        Optional<StockBajoEvent> evento = stockBajoPolicy.evaluar(producto);

        assertThat(evento).isPresent();
        assertThat(evento.get().productoId()).isEqualTo(1L);
        assertThat(evento.get().codigo()).isEqualTo("FIL-001");
        assertThat(evento.get().nombre()).isEqualTo("Filtro de aceite");
        assertThat(evento.get().stockActual()).isEqualTo(5);
        assertThat(evento.get().stockMinimo()).isEqualTo(5);
    }

    @Test
    void evaluar_cuandoElStockEsMenorAlMinimo_debeEmitirEvento() {
        assertThat(stockBajoPolicy.evaluar(productoConStock(1))).isPresent();
    }

    @Test
    void evaluar_cuandoElStockSuperaElMinimo_noDebeEmitirEvento() {
        assertThat(stockBajoPolicy.evaluar(productoConStock(6))).isEmpty();
    }

    @Test
    void evaluar_cuandoElStockEsNulo_noDebeEmitirEvento() {
        Producto producto = new Producto();
        producto.setStockActual(null);

        assertThat(stockBajoPolicy.evaluar(producto)).isEmpty();
    }

    @Test
    void evaluar_cuandoElProductoEsNulo_noDebeEmitirEvento() {
        assertThat(stockBajoPolicy.evaluar(null)).isEmpty();
    }

    @Test
    void constructor_conMinimoNegativo_debeLanzarExcepcion() {
        assertThatThrownBy(() -> new StockBajoPolicy(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativo");
    }

    private Producto productoConStock(int stock) {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setCodigo("FIL-001");
        producto.setNombre("Filtro de aceite");
        producto.setStockActual(stock);
        return producto;
    }
}

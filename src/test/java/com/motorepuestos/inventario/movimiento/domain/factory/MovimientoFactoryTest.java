package com.motorepuestos.inventario.movimiento.domain.factory;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovimientoFactoryTest {

    private final MovimientoFactory movimientoFactory = new MovimientoFactory();

    @Test
    void crear_debeAsociarDetallesYUsuarioAlMovimiento() {
        Usuario usuario = new Usuario();
        usuario.setUsername("usuario.test");

        DetalleMovimiento detalle1 = new DetalleMovimiento();
        DetalleMovimiento detalle2 = new DetalleMovimiento();
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 15, 10, 30);

        Movimiento movimiento = movimientoFactory.crear(
                Tipo.ENTRADA,
                usuario,
                fecha,
                List.of(detalle1, detalle2)
        );

        assertThat(movimiento.getTipo()).isEqualTo(Tipo.ENTRADA);
        assertThat(movimiento.getUsuario()).isSameAs(usuario);
        assertThat(movimiento.getFecha()).isEqualTo(fecha);
        assertThat(movimiento.getDetalles()).containsExactly(detalle1, detalle2);
        assertThat(detalle1.getMovimiento()).isSameAs(movimiento);
        assertThat(detalle2.getMovimiento()).isSameAs(movimiento);
    }

    @Test
    void crear_sinFecha_debeUsarLaFechaActual() {
        Movimiento movimiento = movimientoFactory.crear(
                Tipo.SALIDA,
                new Usuario(),
                null,
                List.of(new DetalleMovimiento())
        );

        assertThat(movimiento.getFecha()).isNotNull();
    }

    @Test
    void crear_sinTipo_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoFactory.crear(
                null,
                new Usuario(),
                LocalDateTime.now(),
                List.of(new DetalleMovimiento())
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("tipo de movimiento");
    }

    @Test
    void crear_sinUsuario_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoFactory.crear(
                Tipo.ENTRADA,
                null,
                LocalDateTime.now(),
                List.of(new DetalleMovimiento())
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("usuario");
    }

    @Test
    void crear_sinDetalles_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoFactory.crear(
                Tipo.ENTRADA,
                new Usuario(),
                LocalDateTime.now(),
                List.of()
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("al menos un detalle");
    }

    @Test
    void crear_conDetallesNulos_debeLanzarExcepcion() {
        assertThatThrownBy(() -> movimientoFactory.crear(
                Tipo.ENTRADA,
                new Usuario(),
                LocalDateTime.now(),
                null
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("al menos un detalle");
    }
}

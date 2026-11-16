package com.motorepuestos.inventario.movimiento.application.query;

import com.motorepuestos.inventario.movimiento.dto.MovimientoResumenResponse;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObtenerResumenMovimientosHoyQueryHandler {

    private final MovimientoRepository movimientoRepository;

    public MovimientoResumenResponse handle(ObtenerResumenMovimientosHoyQuery query) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);

        long entradas = movimientoRepository.sumarCantidadPorTipoEntre(
                Tipo.ENTRADA,
                inicio,
                fin
        );

        long salidas = movimientoRepository.sumarCantidadPorTipoEntre(
                Tipo.SALIDA,
                inicio,
                fin
        );

        return new MovimientoResumenResponse(entradas, salidas);
    }
}

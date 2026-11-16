package com.motorepuestos.inventario.movimiento.service.strategy;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MovimientoTipoStrategyFactory {

    private final Map<Tipo, MovimientoTipoStrategy> estrategias;

    public MovimientoTipoStrategyFactory(List<MovimientoTipoStrategy> estrategias) {
        this.estrategias = new EnumMap<>(Tipo.class);
        for (MovimientoTipoStrategy estrategia : estrategias) {
            this.estrategias.put(estrategia.tipo(), estrategia);
        }
    }

    public MovimientoTipoStrategy obtener(Tipo tipo) {
        if (tipo == null) {
            throw new BusinessException("El tipo de movimiento es obligatorio");
        }
        MovimientoTipoStrategy estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new BusinessException("Tipo de movimiento no soportado: " + tipo);
        }
        return estrategia;
    }
}

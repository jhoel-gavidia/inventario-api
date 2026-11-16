package com.motorepuestos.inventario.movimiento.application.validation;

import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class MovimientoValidacionContext {

    private final List<DetalleMovimientoRequest> detalles;
    private final List<DetalleMovimientoValidado> detallesValidados = new ArrayList<>();

    public MovimientoValidacionContext(List<DetalleMovimientoRequest> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalleValidado(DetalleMovimientoValidado detalle) {
        detallesValidados.add(detalle);
    }

    public void ordenarDetallesValidados() {
        detallesValidados.sort(Comparator.comparing(DetalleMovimientoValidado::productoId));
    }
}

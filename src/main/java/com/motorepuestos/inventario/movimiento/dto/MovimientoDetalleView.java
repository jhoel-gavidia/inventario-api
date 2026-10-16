package com.motorepuestos.inventario.movimiento.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.motorepuestos.inventario.movimiento.entity.Tipo;

import java.time.LocalDateTime;

public record MovimientoDetalleView(
        Long movimientoId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fecha,
        Tipo tipo,
        Long productoId,
        String productoNombre,
        String productoCodigo,
        Integer cantidad
) {
}

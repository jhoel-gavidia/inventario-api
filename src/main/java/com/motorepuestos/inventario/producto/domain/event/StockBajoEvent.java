package com.motorepuestos.inventario.producto.domain.event;

public record StockBajoEvent(
        Long productoId,
        String codigo,
        String nombre,
        int stockActual,
        int stockMinimo
) {
}

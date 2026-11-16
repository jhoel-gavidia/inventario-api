package com.motorepuestos.inventario.producto.domain.policy;

import com.motorepuestos.inventario.producto.domain.event.StockBajoEvent;
import com.motorepuestos.inventario.producto.entity.Producto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StockBajoPolicy {

    private final int stockMinimo;

    public StockBajoPolicy(@Value("${app.stock.minimo:5}") int stockMinimo) {
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        this.stockMinimo = stockMinimo;
    }

    public Optional<StockBajoEvent> evaluar(Producto producto) {
        if (producto == null || producto.getStockActual() == null) {
            return Optional.empty();
        }
        if (producto.getStockActual() > stockMinimo) {
            return Optional.empty();
        }

        return Optional.of(new StockBajoEvent(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getStockActual(),
                stockMinimo
        ));
    }
}

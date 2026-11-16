package com.motorepuestos.inventario.producto.domain.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class StockBajoEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onStockBajo(StockBajoEvent event) {
        log.warn(
                "Stock bajo para el producto {} ({}): {} unidades, mínimo configurado {}",
                event.nombre(),
                event.codigo(),
                event.stockActual(),
                event.stockMinimo()
        );
    }
}

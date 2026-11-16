package com.motorepuestos.inventario.auditoria.event;

import com.motorepuestos.inventario.auditoria.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AuditoriaEventListener {

    private final AuditoriaService auditoriaService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    public void onAuditoria(AuditoriaEvent event) {
        if (event.actor() != null) {
            auditoriaService.registrar(
                    event.actor(),
                    event.accion(),
                    event.entidad(),
                    event.entidadId(),
                    event.datosAnt(),
                    event.datosNew()
            );
        } else {
            auditoriaService.registrar(
                    event.accion(),
                    event.entidad(),
                    event.entidadId(),
                    event.datosAnt(),
                    event.datosNew()
            );
        }
    }
}

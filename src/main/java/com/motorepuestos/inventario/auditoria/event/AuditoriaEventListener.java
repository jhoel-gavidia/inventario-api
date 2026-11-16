package com.motorepuestos.inventario.auditoria.event;

import com.motorepuestos.inventario.auditoria.service.AuditoriaCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AuditoriaEventListener {

    private final AuditoriaCommandService auditoriaCommandService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    public void onAuditoria(AuditoriaEvent event) {
        if (event.actor() != null) {
            auditoriaCommandService.registrar(
                    event.actor(),
                    event.accion(),
                    event.entidad(),
                    event.entidadId(),
                    event.datosAnt(),
                    event.datosNew()
            );
        } else {
            auditoriaCommandService.registrar(
                    event.accion(),
                    event.entidad(),
                    event.entidadId(),
                    event.datosAnt(),
                    event.datosNew()
            );
        }
    }
}

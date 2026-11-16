package com.motorepuestos.inventario.auditoria.event;

import com.motorepuestos.inventario.auditoria.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditoriaEventListener {

    private final AuditoriaService auditoriaService;

    @EventListener
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

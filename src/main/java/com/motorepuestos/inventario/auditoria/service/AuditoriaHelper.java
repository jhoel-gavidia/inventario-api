package com.motorepuestos.inventario.auditoria.service;

import com.motorepuestos.inventario.auditoria.event.AuditoriaEvent;
import com.motorepuestos.inventario.common.util.JsonUtil;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AuditoriaHelper {

    private final ApplicationEventPublisher eventPublisher;
    private final JsonUtil jsonUtil;

    public void registrarCreacion(String accion, String entidad, Long id, Object instantanea) {
        publicar(null, accion, entidad, id, null, serializar(instantanea));
    }

    public void registrar(
            Usuario actor,
            String accion,
            String entidad,
            Long id,
            Object anterior,
            Object nuevo
    ) {
        publicar(actor, accion, entidad, id, serializar(anterior), serializar(nuevo));
    }

    public <E, R> void registrarCambio(
            String accion,
            String entidad,
            Long id,
            E estado,
            Function<E, R> instantanea,
            Runnable mutacion
    ) {
        R anterior = instantanea.apply(estado);
        mutacion.run();
        R nuevo = instantanea.apply(estado);
        publicar(null, accion, entidad, id, serializar(anterior), serializar(nuevo));
    }

    public <E, R> void registrarCambio(
            Usuario actor,
            String accion,
            String entidad,
            Long id,
            E estado,
            Function<E, R> instantanea,
            Runnable mutacion
    ) {
        R anterior = instantanea.apply(estado);
        mutacion.run();
        R nuevo = instantanea.apply(estado);
        publicar(actor, accion, entidad, id, serializar(anterior), serializar(nuevo));
    }

    private void publicar(
            Usuario actor,
            String accion,
            String entidad,
            Long id,
            String datosAnt,
            String datosNew
    ) {
        eventPublisher.publishEvent(new AuditoriaEvent(actor, accion, entidad, id, datosAnt, datosNew));
    }

    private String serializar(Object valor) {
        return valor == null ? null : jsonUtil.convertir(valor);
    }
}
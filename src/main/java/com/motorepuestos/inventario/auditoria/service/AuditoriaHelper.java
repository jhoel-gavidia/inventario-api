package com.motorepuestos.inventario.auditoria.service;

import com.motorepuestos.inventario.common.util.JsonUtil;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AuditoriaHelper {

    private final AuditoriaService auditoriaService;
    private final JsonUtil jsonUtil;

    public void registrarCreacion(String accion, String entidad, Long id, Object instantanea) {
        auditoriaService.registrar(accion, entidad, id, null, serializar(instantanea));
    }

    public void registrar(
            Usuario actor,
            String accion,
            String entidad,
            Long id,
            Object anterior,
            Object nuevo
    ) {
        auditoriaService.registrar(
                actor,
                accion,
                entidad,
                id,
                serializar(anterior),
                serializar(nuevo)
        );
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

        auditoriaService.registrar(
                accion,
                entidad,
                id,
                serializar(anterior),
                serializar(nuevo)
        );
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

        auditoriaService.registrar(
                actor,
                accion,
                entidad,
                id,
                serializar(anterior),
                serializar(nuevo)
        );
    }

    private String serializar(Object valor) {
        return valor == null ? null : jsonUtil.convertir(valor);
    }
}
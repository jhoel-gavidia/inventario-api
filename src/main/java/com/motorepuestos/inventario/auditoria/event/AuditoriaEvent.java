package com.motorepuestos.inventario.auditoria.event;

import com.motorepuestos.inventario.usuario.entity.Usuario;

public record AuditoriaEvent(
        Usuario actor,
        String accion,
        String entidad,
        Long entidadId,
        String datosAnt,
        String datosNew
) {
}

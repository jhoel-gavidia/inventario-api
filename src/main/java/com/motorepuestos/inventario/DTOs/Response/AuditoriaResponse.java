package com.motorepuestos.inventario.DTOs.Response;

import java.time.LocalDateTime;

public record AuditoriaResponse(
        Long id,
        Long usuarioId,
        String username,
        String accion,
        String entidad,
        Long entidadId,
        String datosAnt,
        String datosNew,
        LocalDateTime fecha
) {
}

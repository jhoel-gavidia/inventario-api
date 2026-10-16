package com.motorepuestos.inventario.auditoria.dto;

public record AuditoriaResumenResponse(
        long total,
        long accionesCriticas,
        long usuariosAuditados,
        String entidadMasModificada,
        long entidadMasModificadaTotal
) {
}

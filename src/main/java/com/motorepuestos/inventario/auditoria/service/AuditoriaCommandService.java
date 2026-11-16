package com.motorepuestos.inventario.auditoria.service;

import com.motorepuestos.inventario.usuario.entity.Usuario;

public interface AuditoriaCommandService {

    void registrar(
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    );

    void registrar(
            Usuario usuario,
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    );
}

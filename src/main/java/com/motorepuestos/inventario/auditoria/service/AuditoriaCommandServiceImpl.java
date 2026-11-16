package com.motorepuestos.inventario.auditoria.service;

import com.motorepuestos.inventario.auditoria.entity.Auditoria;
import com.motorepuestos.inventario.auditoria.repository.AuditoriaRepository;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditoriaCommandServiceImpl implements AuditoriaCommandService {

    private final AuditoriaRepository auditoriaRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public void registrar(
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    ) {
        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();
        validarInvariantes(usuario, accion, entidad, entidadId);
        guardar(usuario, accion, entidad, entidadId, datosAnt, datosNew);
    }

    @Override
    @Transactional
    public void registrar(
            Usuario usuario,
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    ) {
        validarInvariantes(usuario, accion, entidad, entidadId);
        guardar(usuario, accion, entidad, entidadId, datosAnt, datosNew);
    }

    private void validarInvariantes(
            Usuario usuario,
            String accion,
            String entidad,
            Long entidadId
    ) {
        if (usuario == null) {
            throw new IllegalStateException(
                    "No hay usuario autenticado para auditar " + accion + " en " + entidad
            );
        }

        if (entidadId == null) {
            throw new IllegalArgumentException(
                    "entidadId es obligatorio para auditar " + accion + " en " + entidad
            );
        }
    }

    private void guardar(
            Usuario usuario,
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    ) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setDatosAnt(datosAnt);
        auditoria.setDatosNew(datosNew);
        auditoria.setFecha(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
    }
}

package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;
import com.motorepuestos.inventario.DTOs.Response.PaginaResponse;
import com.motorepuestos.inventario.entity.Auditoria;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.AuditoriaMapper;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.security.SecurityUtils;
import com.motorepuestos.inventario.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final SecurityUtils securityUtils;
    private final AuditoriaMapper auditoriaMapper;

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

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<AuditoriaResponse> listar(Pageable pageable) {
        return PaginaResponse.from(
                auditoriaRepository.findAllByOrderByFechaDesc(pageable),
                auditoriaMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<AuditoriaResponse> listarPorEntidad(
            String entidad,
            Long entidadId,
            Pageable pageable
    ) {
        return PaginaResponse.from(
                auditoriaRepository.findByEntidadAndEntidadIdOrderByFechaDesc(
                        entidad,
                        entidadId,
                        pageable
                ),
                auditoriaMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaResponse obtenerPorId(Long id) {
        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Auditoría no encontrada con id: " + id
                        )
                );

        return auditoriaMapper.toResponse(auditoria);
    }
}
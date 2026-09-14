package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;
import com.motorepuestos.inventario.entity.Auditoria;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.AuditoriaMapper;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.security.SecurityUtils;
import com.motorepuestos.inventario.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final SecurityUtils securityUtils;
    private final AuditoriaMapper auditoriaMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    ) {
        try {
            Usuario usuario = securityUtils.obtenerUsuarioAutenticado();

            Auditoria auditoria = new Auditoria();
            auditoria.setUsuario(usuario);
            auditoria.setAccion(accion);
            auditoria.setEntidad(entidad);
            auditoria.setEntidadId(entidadId);
            auditoria.setDatosAnt(datosAnt);
            auditoria.setDatosNew(datosNew);
            auditoria.setFecha(LocalDateTime.now());

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error al registrar auditoría: acción={}, entidad={}, id={}",
                    accion, entidad, entidadId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listar() {
        return auditoriaRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(auditoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarPorEntidad(String entidad, Long entidadId) {
        return auditoriaRepository
                .findByEntidadAndEntidadIdOrderByFechaDesc(entidad, entidadId)
                .stream()
                .map(auditoriaMapper::toResponse)
                .toList();
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
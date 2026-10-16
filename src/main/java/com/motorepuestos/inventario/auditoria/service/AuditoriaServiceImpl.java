package com.motorepuestos.inventario.auditoria.service;

import com.motorepuestos.inventario.auditoria.dto.AuditoriaResponse;
import com.motorepuestos.inventario.auditoria.dto.AuditoriaResumenResponse;
import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.auditoria.entity.Auditoria;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.auditoria.mapper.AuditoriaMapper;
import com.motorepuestos.inventario.auditoria.repository.AuditoriaRepository;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public PaginaResponse<AuditoriaResponse> buscar(
            String search,
            String entidad,
            Long entidadId,
            String accion,
            LocalDate desde,
            Pageable pageable
    ) {
        Specification<Auditoria> specification = construirFiltro(
                search,
                entidad,
                entidadId,
                accion,
                desde
        );

        Page<Auditoria> page = auditoriaRepository.findAll(specification, pageable);

        return PaginaResponse.from(page, auditoriaMapper::toResponse);
    }

    private Specification<Auditoria> construirFiltro(
            String search,
            String entidad,
            Long entidadId,
            String accion,
            LocalDate desde
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (entidad != null && !entidad.isBlank()) {
                predicates.add(
                        cb.equal(root.get("entidad"), entidad.trim().toUpperCase())
                );
            }

            if (entidadId != null) {
                predicates.add(cb.equal(root.get("entidadId"), entidadId));
            }

            if (accion != null && !accion.isBlank()) {
                predicates.add(
                        cb.equal(root.get("accion"), accion.trim().toUpperCase())
                );
            }

            if (desde != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("fecha"), desde.atStartOfDay())
                );
            }

            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim().toLowerCase() + "%";

                Join<Auditoria, Usuario> usuario = root.join("usuario", JoinType.INNER);

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("entidad")), like),
                                cb.like(cb.lower(usuario.get("username")), like),
                                cb.like(cb.lower(root.get("id").as(String.class)), like),
                                cb.like(
                                        cb.lower(root.get("entidadId").as(String.class)),
                                        like
                                )
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaResumenResponse resumen() {
        long total = auditoriaRepository.count();
        long accionesCriticas = auditoriaRepository.countByAccion("DELETE");
        long usuariosAuditados = auditoriaRepository.contarUsuariosAuditados();

        List<Object[]> ranking = auditoriaRepository.rankingEntidades(
                PageRequest.of(0, 1)
        );

        String entidadMasModificada = null;
        long entidadMasModificadaTotal = 0;

        if (!ranking.isEmpty()) {
            Object[] fila = ranking.get(0);

            if (fila[0] != null) {
                entidadMasModificada = (String) fila[0];
            }

            entidadMasModificadaTotal = ((Number) fila[1]).longValue();
        }

        return new AuditoriaResumenResponse(
                total,
                accionesCriticas,
                usuariosAuditados,
                entidadMasModificada,
                entidadMasModificadaTotal
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
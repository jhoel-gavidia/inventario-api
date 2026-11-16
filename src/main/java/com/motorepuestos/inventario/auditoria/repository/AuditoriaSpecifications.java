package com.motorepuestos.inventario.auditoria.repository;

import com.motorepuestos.inventario.auditoria.entity.Auditoria;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class AuditoriaSpecifications {

    private AuditoriaSpecifications() {
    }

    public static Specification<Auditoria> conEntidad(String entidad) {
        return (root, query, cb) -> {
            if (entidad == null || entidad.isBlank()) {
                return null;
            }
            return cb.equal(root.get("entidad"), entidad.trim().toUpperCase());
        };
    }

    public static Specification<Auditoria> conEntidadId(Long entidadId) {
        return (root, query, cb) -> entidadId == null ? null : cb.equal(root.get("entidadId"), entidadId);
    }

    public static Specification<Auditoria> conAccion(String accion) {
        return (root, query, cb) -> {
            if (accion == null || accion.isBlank()) {
                return null;
            }
            return cb.equal(root.get("accion"), accion.trim().toUpperCase());
        };
    }

    public static Specification<Auditoria> desde(LocalDate desde) {
        return (root, query, cb) -> desde == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("fecha"), desde.atStartOfDay());
    }

    public static Specification<Auditoria> conTexto(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.trim().toLowerCase() + "%";
            Join<Auditoria, Usuario> usuario = root.join("usuario", JoinType.INNER);
            return cb.or(
                    cb.like(cb.lower(root.get("entidad")), like),
                    cb.like(cb.lower(usuario.get("username")), like),
                    cb.like(cb.lower(root.get("id").as(String.class)), like),
                    cb.like(cb.lower(root.get("entidadId").as(String.class)), like)
            );
        };
    }
}

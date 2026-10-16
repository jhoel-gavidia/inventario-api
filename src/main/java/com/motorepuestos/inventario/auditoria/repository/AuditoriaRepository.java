package com.motorepuestos.inventario.auditoria.repository;

import com.motorepuestos.inventario.auditoria.entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditoriaRepository extends
        JpaRepository<Auditoria, Long>,
        JpaSpecificationExecutor<Auditoria> {

    @EntityGraph(attributePaths = {"usuario"})
    Page<Auditoria> findByEntidadAndEntidadIdOrderByFechaDesc(
            String entidad,
            Long entidadId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"usuario"})
    Page<Auditoria> findAllByOrderByFechaDesc(Pageable pageable);

    long countByAccion(String accion);

    @Query("select count(distinct a.usuario.id) from Auditoria a")
    long contarUsuariosAuditados();

    @Query("""
            select a.entidad, count(a)
            from Auditoria a
            group by a.entidad
            order by count(a) desc, a.entidad asc
            """)
    List<Object[]> rankingEntidades(Pageable pageable);
}

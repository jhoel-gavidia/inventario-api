package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @EntityGraph(attributePaths = {"usuario"})
    Page<Auditoria> findByEntidadAndEntidadIdOrderByFechaDesc(
            String entidad,
            Long entidadId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"usuario"})
    Page<Auditoria> findAllByOrderByFechaDesc(Pageable pageable);
}
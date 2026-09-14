package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Auditoria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @EntityGraph(attributePaths = {"usuario"})
    List<Auditoria> findByEntidadAndEntidadIdOrderByFechaDesc(String entidad, Long entidadId);

    @EntityGraph(attributePaths = {"usuario"})
    List<Auditoria> findAllByOrderByFechaDesc();
}

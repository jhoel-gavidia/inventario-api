package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Auditoria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @EntityGraph(attributePaths = {"usuario"})
    Optional<Auditoria> findByEntidadAndEntidadId(String entidad, Long entidadId);

}

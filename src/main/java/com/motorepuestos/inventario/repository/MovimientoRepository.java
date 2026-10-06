package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto"})
    Page<Movimiento> findAllBy(Pageable pageable);
}
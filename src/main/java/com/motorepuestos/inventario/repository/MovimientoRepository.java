package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
}

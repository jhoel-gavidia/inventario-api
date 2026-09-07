package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {
}

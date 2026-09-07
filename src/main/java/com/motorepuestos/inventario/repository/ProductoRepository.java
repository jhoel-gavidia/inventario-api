package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);
}

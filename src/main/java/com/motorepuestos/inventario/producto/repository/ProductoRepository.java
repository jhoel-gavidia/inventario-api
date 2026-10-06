package com.motorepuestos.inventario.producto.repository;

import com.motorepuestos.inventario.producto.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndEstadoTrue(String codigo);

    List<Producto> findByEstadoTrue();

    Optional<Producto> findByIdAndEstadoTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);

    boolean existsByCategoriaIdAndEstadoTrue(Long categoriaId);
}

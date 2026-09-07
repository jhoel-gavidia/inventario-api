package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(Long id);
    boolean existsByCategoriaId(Long categoriaId);
}

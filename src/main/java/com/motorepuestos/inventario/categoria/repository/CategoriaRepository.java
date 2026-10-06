package com.motorepuestos.inventario.categoria.repository;

import com.motorepuestos.inventario.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndEstadoTrue(String nombre);

    List<Categoria> findByEstadoTrueOrderByNombreAsc();

    Optional<Categoria> findByIdAndEstadoTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Categoria c WHERE c.id = :id")
    Optional<Categoria> findByIdForUpdate(@Param("id") Long id);
}

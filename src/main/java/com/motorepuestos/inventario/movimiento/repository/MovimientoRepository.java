package com.motorepuestos.inventario.movimiento.repository;

import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto"})
    Page<Movimiento> findAllBy(Pageable pageable);

    @Query(
            value = """
                    select new com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView(
                        m.id, m.fecha, m.tipo, p.id, p.nombre, p.codigo, d.cantidad)
                    from Movimiento m
                    join m.detalles d
                    join d.producto p
                    where (:search is null
                           or lower(p.nombre) like :search
                           or lower(p.codigo) like :search)
                    order by m.fecha desc, m.id desc
                    """,
            countQuery = """
                    select count(d)
                    from Movimiento m
                    join m.detalles d
                    join d.producto p
                    where (:search is null
                           or lower(p.nombre) like :search
                           or lower(p.codigo) like :search)
                    """
    )
    Page<MovimientoDetalleView> buscarDetalles(
            @Param("search") String search,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView(
                        m.id, m.fecha, m.tipo, p.id, p.nombre, p.codigo, d.cantidad)
                    from Movimiento m
                    join m.detalles d
                    join d.producto p
                    where m.tipo = :tipo
                      and (:search is null
                           or lower(p.nombre) like :search
                           or lower(p.codigo) like :search)
                    order by m.fecha desc, m.id desc
                    """,
            countQuery = """
                    select count(d)
                    from Movimiento m
                    join m.detalles d
                    join d.producto p
                    where m.tipo = :tipo
                      and (:search is null
                           or lower(p.nombre) like :search
                           or lower(p.codigo) like :search)
                    """
    )
    Page<MovimientoDetalleView> buscarDetallesPorTipo(
            @Param("tipo") Tipo tipo,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            select coalesce(sum(d.cantidad), 0L)
            from Movimiento m
            join m.detalles d
            where m.tipo = :tipo
              and m.fecha >= :desde
              and m.fecha < :hasta
            """)
    long sumarCantidadPorTipoEntre(
            @Param("tipo") Tipo tipo,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}

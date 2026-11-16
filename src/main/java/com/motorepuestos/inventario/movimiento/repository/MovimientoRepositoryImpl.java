package com.motorepuestos.inventario.movimiento.repository;

import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.producto.entity.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovimientoRepositoryImpl implements MovimientoRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<MovimientoDetalleView> buscarDetalles(Tipo tipo, String searchNormalizado, Pageable pageable) {
        Specification<DetalleMovimiento> specification = Specification
                .where(MovimientoDetalleSpecifications.conTipo(tipo))
                .and(MovimientoDetalleSpecifications.conBusquedaProducto(searchNormalizado));

        List<MovimientoDetalleView> content = fetchContent(specification, pageable);
        long total = fetchCount(specification);

        return new PageImpl<>(content, pageable, total);
    }

    private List<MovimientoDetalleView> fetchContent(Specification<DetalleMovimiento> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MovimientoDetalleView> cq = cb.createQuery(MovimientoDetalleView.class);
        Root<DetalleMovimiento> root = cq.from(DetalleMovimiento.class);

        Predicate predicate = specification.toPredicate(root, cq, cb);
        Join<DetalleMovimiento, Movimiento> movimiento = MovimientoDetalleSpecifications.joinMovimiento(root);
        Join<DetalleMovimiento, Producto> producto = MovimientoDetalleSpecifications.joinProducto(root);

        if (predicate != null) {
            cq.where(predicate);
        }

        cq.select(cb.construct(
                MovimientoDetalleView.class,
                movimiento.get("id"),
                movimiento.get("fecha"),
                movimiento.get("tipo"),
                producto.get("id"),
                producto.get("nombre"),
                producto.get("codigo"),
                root.get("cantidad")
        ));

        cq.orderBy(resolveOrders(cb, movimiento, pageable.getSort()));

        TypedQuery<MovimientoDetalleView> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

    private long fetchCount(Specification<DetalleMovimiento> specification) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DetalleMovimiento> root = cq.from(DetalleMovimiento.class);

        Predicate predicate = specification.toPredicate(root, cq, cb);
        if (predicate != null) {
            cq.where(predicate);
        }
        cq.select(cb.count(root));

        return entityManager.createQuery(cq).getSingleResult();
    }

    private List<Order> resolveOrders(CriteriaBuilder cb, Join<DetalleMovimiento, Movimiento> movimiento, Sort sort) {
        List<Order> orders = new ArrayList<>();
        if (sort == null || sort.isUnsorted()) {
            orders.add(cb.desc(movimiento.get("fecha")));
            orders.add(cb.desc(movimiento.get("id")));
            return orders;
        }

        sort.forEach(order -> {
            String property = mapSortProperty(order.getProperty());
            jakarta.persistence.criteria.Path<?> path = "id".equals(property)
                    ? movimiento.get("id")
                    : movimiento.get(property);
            orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
        });

        if (orders.isEmpty()) {
            orders.add(cb.desc(movimiento.get("fecha")));
            orders.add(cb.desc(movimiento.get("id")));
        }
        return orders;
    }

    private String mapSortProperty(String property) {
        if (property == null || property.isBlank()) {
            return "fecha";
        }
        return switch (property) {
            case "id", "movimientoId" -> "id";
            case "fecha" -> "fecha";
            default -> "fecha";
        };
    }
}

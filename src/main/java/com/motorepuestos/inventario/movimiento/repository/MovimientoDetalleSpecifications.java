package com.motorepuestos.inventario.movimiento.repository;

import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.producto.entity.Producto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public final class MovimientoDetalleSpecifications {

    private MovimientoDetalleSpecifications() {
    }

    public static Specification<DetalleMovimiento> conTipo(Tipo tipo) {
        return (root, query, cb) -> {
            if (tipo == null) {
                return null;
            }
            Join<DetalleMovimiento, Movimiento> movimiento = joinMovimiento(root);
            return cb.equal(movimiento.get("tipo"), tipo);
        };
    }

    public static Specification<DetalleMovimiento> conBusquedaProducto(String searchNormalizado) {
        return (root, query, cb) -> {
            if (searchNormalizado == null || searchNormalizado.isBlank()) {
                return null;
            }
            Join<DetalleMovimiento, Producto> producto = joinProducto(root);
            return cb.or(
                    cb.like(cb.lower(producto.get("nombre")), searchNormalizado),
                    cb.like(cb.lower(producto.get("codigo")), searchNormalizado)
            );
        };
    }

    @SuppressWarnings("unchecked")
    public static Join<DetalleMovimiento, Movimiento> joinMovimiento(Root<DetalleMovimiento> root) {
        return root.getJoins().stream()
                .filter(join -> "movimiento".equals(join.getAttribute().getName()))
                .map(join -> (Join<DetalleMovimiento, Movimiento>) join)
                .findFirst()
                .orElseGet(() -> root.join("movimiento"));
    }

    @SuppressWarnings("unchecked")
    public static Join<DetalleMovimiento, Producto> joinProducto(Root<DetalleMovimiento> root) {
        return root.getJoins().stream()
                .filter(join -> "producto".equals(join.getAttribute().getName()))
                .map(join -> (Join<DetalleMovimiento, Producto>) join)
                .findFirst()
                .orElseGet(() -> root.join("producto"));
    }
}

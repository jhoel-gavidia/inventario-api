package com.motorepuestos.inventario.producto.repository;

import com.motorepuestos.inventario.producto.entity.Producto;
import org.springframework.data.jpa.domain.Specification;

public final class ProductoSpecifications {

    private ProductoSpecifications() {
    }

    public static Specification<Producto> conBusqueda(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("codigo")), like),
                    cb.like(cb.lower(root.get("nombre")), like)
            );
        };
    }

    public static Specification<Producto> conEstado(Boolean estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Producto> conCategoriaId(Long categoriaId) {
        return (root, query, cb) -> categoriaId == null
                ? null
                : cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Producto> conStockMaximo(Integer stockMaximo) {
        return (root, query, cb) -> stockMaximo == null
                ? null
                : cb.lessThanOrEqualTo(root.get("stockActual"), stockMaximo);
    }
}

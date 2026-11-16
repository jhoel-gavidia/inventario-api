package com.motorepuestos.inventario.categoria.repository;

import com.motorepuestos.inventario.categoria.entity.Categoria;
import org.springframework.data.jpa.domain.Specification;

public final class CategoriaSpecifications {

    private CategoriaSpecifications() {
    }

    public static Specification<Categoria> conBusqueda(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("nombre")), like);
        };
    }

    public static Specification<Categoria> conEstado(Boolean estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }
}

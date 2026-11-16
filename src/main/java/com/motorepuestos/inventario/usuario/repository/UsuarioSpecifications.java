package com.motorepuestos.inventario.usuario.repository;

import com.motorepuestos.inventario.usuario.entity.Rol;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import org.springframework.data.jpa.domain.Specification;

public final class UsuarioSpecifications {

    private UsuarioSpecifications() {
    }

    public static Specification<Usuario> conBusqueda(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("username")), like);
        };
    }

    public static Specification<Usuario> conRol(Rol rol) {
        return (root, query, cb) -> rol == null ? null : cb.equal(root.get("rol"), rol);
    }

    public static Specification<Usuario> conEstado(Boolean estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }
}

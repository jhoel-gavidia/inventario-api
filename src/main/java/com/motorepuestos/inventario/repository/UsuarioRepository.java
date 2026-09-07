package com.motorepuestos.inventario.repository;

import com.motorepuestos.inventario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository  extends JpaRepository<Usuario, Long> {
}

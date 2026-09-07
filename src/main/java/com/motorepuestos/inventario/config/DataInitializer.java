package com.motorepuestos.inventario.config;

import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (usuarioRepository.existsByUsername("admin")) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword(passwordEncoder.encode("123456"));
        usuario.setRol(Rol.ADMIN);
        usuario.setEstado(true);

        usuarioRepository.save(usuario);
    }
}
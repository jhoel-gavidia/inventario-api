package com.motorepuestos.inventario.usuario.domain.policy;

import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import com.motorepuestos.inventario.usuario.domain.valueobject.Username;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioUsernamePolicy {

    private final UsuarioRepository usuarioRepository;

    public Username validarYNormalizar(String username) {
        return Username.de(username);
    }

    public void validarDisponibleParaCreacion(Username username) {
        if (usuarioRepository.existsByUsername(username.valor())) {
            throw new ResourceConflictException("El username ya existe");
        }
    }

    public void validarDisponibleParaActualizacion(Usuario usuario, Username nuevoUsername) {
        boolean cambioUsername = !usuario.getUsername().equals(nuevoUsername.valor());
        if (cambioUsername && usuarioRepository.existsByUsername(nuevoUsername.valor())) {
            throw new ResourceConflictException("El username ya existe");
        }
    }
}

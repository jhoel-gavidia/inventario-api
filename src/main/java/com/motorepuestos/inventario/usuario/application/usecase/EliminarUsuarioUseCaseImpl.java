package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.usuario.application.command.EliminarUsuarioCommand;
import com.motorepuestos.inventario.usuario.domain.policy.UsuarioAccesoPolicy;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarUsuarioUseCaseImpl implements EliminarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final AuditoriaHelper auditoriaHelper;
    private final SecurityUtils securityUtils;
    private final UsuarioAccesoPolicy usuarioAccesoPolicy;

    @Override
    @Transactional
    public void ejecutar(EliminarUsuarioCommand command) {
        Usuario usuario = obtenerUsuarioConLockOrThrow(command.id());

        Usuario actor = securityUtils.obtenerUsuarioAutenticado();
        usuarioAccesoPolicy.validarCambioDeAcceso(usuario, usuario.getRol(), false);

        auditoriaHelper.registrarCambio(
                actor,
                "ELIMINAR",
                "USUARIO",
                command.id(),
                usuario,
                usuarioMapper::toResponse,
                () -> usuario.setEstado(false)
        );
    }

    private Usuario obtenerUsuarioConLockOrThrow(Long id) {
        return usuarioRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}

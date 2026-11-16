package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.usuario.application.command.ActualizarUsuarioCommand;
import com.motorepuestos.inventario.usuario.domain.policy.UsuarioAccesoPolicy;
import com.motorepuestos.inventario.usuario.domain.policy.UsuarioUsernamePolicy;
import com.motorepuestos.inventario.usuario.domain.valueobject.Username;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;
import com.motorepuestos.inventario.usuario.dto.UsuarioUpdateRequest;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarUsuarioUseCaseImpl implements ActualizarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final AuditoriaHelper auditoriaHelper;
    private final SecurityUtils securityUtils;
    private final UsuarioAccesoPolicy usuarioAccesoPolicy;
    private final UsuarioUsernamePolicy usuarioUsernamePolicy;

    @Override
    @Transactional
    public UsuarioResponse ejecutar(ActualizarUsuarioCommand command) {
        UsuarioUpdateRequest request = command.request();
        Usuario usuario = obtenerUsuarioConLockOrThrow(command.id());

        // Se resuelve el actor antes de mutar el username: si el usuario se
        // renombra a sí mismo, después ya no se resuelve por el contexto.
        Usuario actor = securityUtils.obtenerUsuarioAutenticado();

        usuarioAccesoPolicy.validarCambioDeAcceso(usuario, request.getRol(), request.getEstado());

        Username username = usuarioUsernamePolicy.validarYNormalizar(request.getUsername());
        usuarioUsernamePolicy.validarDisponibleParaActualizacion(usuario, username);

        auditoriaHelper.registrarCambio(
                actor,
                "ACTUALIZAR",
                "USUARIO",
                command.id(),
                usuario,
                usuarioMapper::toResponse,
                () -> {
                    usuario.setUsername(username.valor());
                    usuario.setRol(request.getRol());
                    usuario.setEstado(request.getEstado());
                }
        );

        return usuarioMapper.toResponse(usuario);
    }

    private Usuario obtenerUsuarioConLockOrThrow(Long id) {
        return usuarioRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}

package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.usuario.application.command.EliminarUsuarioCommand;
import com.motorepuestos.inventario.usuario.entity.Rol;
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

    @Override
    @Transactional
    public void ejecutar(EliminarUsuarioCommand command) {
        Usuario usuario = obtenerUsuarioConLockOrThrow(command.id());

        Usuario actor = securityUtils.obtenerUsuarioAutenticado();
        validarCambioDeAcceso(usuario, usuario.getRol(), false);

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

    private void validarCambioDeAcceso(
            Usuario usuario,
            Rol nuevoRol,
            boolean nuevoEstado
    ) {
        boolean yaEstaInactivo = !Boolean.TRUE.equals(usuario.getEstado());

        if (yaEstaInactivo && !nuevoEstado) {
            throw new BusinessException("No se puede desactivar un usuario ya inactivo");
        }

        Rol rolActual = usuario.getRol();

        boolean dejaDeSerRolCriticoActivo = rolActual.exigePresencia()
                && !yaEstaInactivo
                && (nuevoRol != rolActual || !nuevoEstado);

        if (dejaDeSerRolCriticoActivo
                && usuarioRepository.countByRolAndEstadoTrue(rolActual) <= 1) {
            throw new BusinessException(
                    "No se puede desactivar ni degradar al único "
                            + rolActual.descripcion() + " activo"
            );
        }
    }
}

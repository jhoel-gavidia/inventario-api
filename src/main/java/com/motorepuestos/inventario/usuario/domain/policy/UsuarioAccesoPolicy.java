package com.motorepuestos.inventario.usuario.domain.policy;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.usuario.entity.Rol;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioAccesoPolicy {

    private final UsuarioRepository usuarioRepository;

    public void validarCambioDeAcceso(Usuario usuario, Rol nuevoRol, boolean nuevoEstado) {
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

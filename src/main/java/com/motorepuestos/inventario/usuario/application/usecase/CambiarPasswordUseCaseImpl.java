package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.usuario.application.command.CambiarPasswordCommand;
import com.motorepuestos.inventario.usuario.dto.CambiarPasswordRequest;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CambiarPasswordUseCaseImpl implements CambiarPasswordUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaHelper auditoriaHelper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public void ejecutar(CambiarPasswordCommand command) {
        CambiarPasswordRequest request = command.request();
        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BadCredentialsException("La contraseña actual no es correcta");
        }

        if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())) {
            throw new BusinessException("La nueva contraseña debe ser distinta de la actual");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);

        auditoriaHelper.registrar(
                usuario,
                "CAMBIAR_PASSWORD",
                "AUTH",
                usuario.getId(),
                null,
                Map.of("cambio", true)
        );
    }
}

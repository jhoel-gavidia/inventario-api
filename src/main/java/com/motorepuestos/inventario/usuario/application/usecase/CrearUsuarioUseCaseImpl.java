package com.motorepuestos.inventario.usuario.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import com.motorepuestos.inventario.usuario.application.command.CrearUsuarioCommand;
import com.motorepuestos.inventario.usuario.dto.UsuarioRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaHelper auditoriaHelper;

    @Override
    @Transactional
    public UsuarioResponse ejecutar(CrearUsuarioCommand command) {
        UsuarioRequest request = command.request();
        validarUsuarioDisponible(request.getUsername());

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuarioRepository.save(usuario);

        UsuarioResponse response = usuarioMapper.toResponse(usuario);
        auditoriaHelper.registrarCreacion("CREAR", "USUARIO", usuario.getId(), response);
        return response;
    }

    private void validarUsuarioDisponible(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResourceConflictException("El username ya existe");
        }
    }
}

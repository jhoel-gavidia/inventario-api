package com.motorepuestos.inventario.usuario.service;

import com.motorepuestos.inventario.usuario.dto.UsuarioRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioUpdateRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;
import com.motorepuestos.inventario.usuario.entity.Rol;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.usuario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaHelper auditoriaHelper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {

        validarUsuarioDisponible(request.getUsername());

        Usuario usuario = usuarioMapper.toEntity(request);

        usuario.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        usuarioRepository.save(usuario);

        UsuarioResponse response = usuarioMapper.toResponse(usuario);

        auditoriaHelper.registrarCreacion("CREAR", "USUARIO", usuario.getId(), response);

        return response;
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.toResponse(obtenerUsuarioOrThrow(id));
    }

    @Override
    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findByEstadoTrue()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = obtenerUsuarioConLockOrThrow(id);

        // Se resuelve el actor antes de mutar el username: si el usuario se
        // renombra a sí mismo, después ya no se resuelve por el contexto.
        Usuario actor = securityUtils.obtenerUsuarioAutenticado();

        validarCambioDeAcceso(usuario, request.getRol(), request.getEstado());

        if (!usuario.getUsername().equals(request.getUsername())
                && usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("El username ya existe");
        }

        auditoriaHelper.registrarCambio(
                actor,
                "ACTUALIZAR",
                "USUARIO",
                id,
                usuario,
                usuarioMapper::toResponse,
                () -> {
                    usuario.setUsername(request.getUsername());
                    usuario.setRol(request.getRol());
                    usuario.setEstado(request.getEstado());
                }
        );

        return usuarioMapper.toResponse(usuario);
    }

    @Override
    public UsuarioResponse obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toResponse)
                .orElse(null);

    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerUsuarioConLockOrThrow(id);

        Usuario actor = securityUtils.obtenerUsuarioAutenticado();

        validarCambioDeAcceso(usuario, usuario.getRol(), false);

        auditoriaHelper.registrarCambio(
                actor,
                "ELIMINAR",
                "USUARIO",
                id,
                usuario,
                usuarioMapper::toResponse,
                () -> usuario.setEstado(false)
        );
    }

    private Usuario obtenerUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
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

    private void validarUsuarioDisponible(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResourceConflictException("El username ya existe");
        }
    }
}


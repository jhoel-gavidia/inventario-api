package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.UsuarioRequest;
import com.motorepuestos.inventario.DTOs.Request.UsuarioUpdateRequest;
import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;
import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.exception.BusinessException;
import com.motorepuestos.inventario.exception.ResourceConflictException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.security.SecurityUtils;
import com.motorepuestos.inventario.service.AuditoriaService;
import com.motorepuestos.inventario.service.UsuarioService;
import com.motorepuestos.inventario.util.JsonUtil;
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
    private final AuditoriaService auditoriaService;
    private final JsonUtil jsonUtil;
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

        auditoriaService.registrar(
                "CREAR",
                "USUARIO",
                usuario.getId(),
                null,
                jsonUtil.convertir(response)
        );

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

        UsuarioResponse datosAntResponse = usuarioMapper.toResponse(usuario);

        if (!usuario.getUsername().equals(request.getUsername())
                && usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ResourceConflictException("El username ya existe");
        }

        usuario.setUsername(request.getUsername());
        usuario.setRol(request.getRol());
        usuario.setEstado(request.getEstado());

        UsuarioResponse datosNewResponse = usuarioMapper.toResponse(usuario);

        auditoriaService.registrar(
                actor,
                "ACTUALIZAR",
                "USUARIO",
                usuario.getId(),
                jsonUtil.convertir(datosAntResponse),
                jsonUtil.convertir(datosNewResponse)
        );

        return datosNewResponse;
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

        UsuarioResponse datosAntResponse = usuarioMapper.toResponse(usuario);

        usuario.setEstado(false);

        UsuarioResponse datosNewResponse = usuarioMapper.toResponse(usuario);

        auditoriaService.registrar(
                actor,
                "ELIMINAR",
                "USUARIO",
                usuario.getId(),
                jsonUtil.convertir(datosAntResponse),
                jsonUtil.convertir(datosNewResponse)
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

        boolean eraAdminActivo = usuario.getRol() == Rol.ADMIN && !yaEstaInactivo;

        boolean dejaDeSerAdminActivo = eraAdminActivo
                && (nuevoRol != Rol.ADMIN || !nuevoEstado);

        if (dejaDeSerAdminActivo
                && usuarioRepository.countByRolAndEstadoTrue(Rol.ADMIN) <= 1) {
            throw new BusinessException(
                    "No se puede desactivar ni degradar al único administrador activo"
            );
        }
    }

    private void validarUsuarioDisponible(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResourceConflictException("El username ya existe");
        }
    }
}


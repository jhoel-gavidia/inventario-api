package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.UsuarioRequest;
import com.motorepuestos.inventario.DTOs.Request.UsuarioUpdateRequest;
import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.exception.ResourceConflictException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.repository.UsuarioRepository;
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
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = obtenerUsuarioOrThrow(id);

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
                "ACTUALIZAR",
                "USUARIO",
                usuario.getId(),
                jsonUtil.convertir(datosAntResponse),
                jsonUtil.convertir(datosNewResponse)
        );

        return datosNewResponse;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerUsuarioOrThrow(id);

        UsuarioResponse datosAntResponse = usuarioMapper.toResponse(usuario);

        usuario.setEstado(false);

        UsuarioResponse datosNewResponse = usuarioMapper.toResponse(usuario);

        auditoriaService.registrar(
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

    private void validarUsuarioDisponible(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResourceConflictException("El username ya existe");
        }
    }
}


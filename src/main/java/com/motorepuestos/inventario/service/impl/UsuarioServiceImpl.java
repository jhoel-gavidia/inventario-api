package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.UsuarioRequest;
import com.motorepuestos.inventario.DTOs.Request.UsuarioUpdateRequest;
import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.service.UsuarioService;
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

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {

        validarUsuarioDisponible(request.getUsername());

        Usuario usuario = usuarioMapper.toEntity(request);

        usuario.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
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

        if (!usuario.getUsername().equals(request.getUsername())
                && usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya existe");
        }

        usuario.setUsername(request.getUsername());
        usuario.setRol(request.getRol());
        usuario.setEstado(request.getEstado());

        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerUsuarioOrThrow(id);

        usuario.setEstado(false);
    }

    private Usuario obtenerUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void validarUsuarioDisponible(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El username ya existe");
        }
    }
}


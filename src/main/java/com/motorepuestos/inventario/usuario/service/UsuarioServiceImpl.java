package com.motorepuestos.inventario.usuario.service;

import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.usuario.application.command.ActualizarUsuarioCommand;
import com.motorepuestos.inventario.usuario.application.command.CambiarPasswordCommand;
import com.motorepuestos.inventario.usuario.application.command.CrearUsuarioCommand;
import com.motorepuestos.inventario.usuario.application.command.EliminarUsuarioCommand;
import com.motorepuestos.inventario.usuario.application.usecase.ActualizarUsuarioUseCase;
import com.motorepuestos.inventario.usuario.application.usecase.CambiarPasswordUseCase;
import com.motorepuestos.inventario.usuario.application.usecase.CrearUsuarioUseCase;
import com.motorepuestos.inventario.usuario.application.usecase.EliminarUsuarioUseCase;
import com.motorepuestos.inventario.usuario.dto.CambiarPasswordRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioRequest;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;
import com.motorepuestos.inventario.usuario.dto.UsuarioUpdateRequest;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final CambiarPasswordUseCase cambiarPasswordUseCase;

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        return crearUsuarioUseCase.ejecutar(new CrearUsuarioCommand(request));
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
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        return actualizarUsuarioUseCase.ejecutar(new ActualizarUsuarioCommand(id, request));
    }

    @Override
    public UsuarioResponse obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toResponse)
                .orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        eliminarUsuarioUseCase.ejecutar(new EliminarUsuarioCommand(id));
    }

    @Override
    public void cambiarPassword(CambiarPasswordRequest request) {
        cambiarPasswordUseCase.ejecutar(new CambiarPasswordCommand(request));
    }

    private Usuario obtenerUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}


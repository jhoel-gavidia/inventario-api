package com.motorepuestos.inventario.usuario.service;

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
import com.motorepuestos.inventario.usuario.service.query.UsuarioQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final CambiarPasswordUseCase cambiarPasswordUseCase;
    private final UsuarioQueryService usuarioQueryService;

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        return crearUsuarioUseCase.ejecutar(new CrearUsuarioCommand(request));
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioQueryService.obtenerPorId(id);
    }

    @Override
    public List<UsuarioResponse> obtenerTodos(String search, com.motorepuestos.inventario.usuario.entity.Rol rol, Boolean estado) {
        return usuarioQueryService.obtenerTodos(search, rol, estado);
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        return actualizarUsuarioUseCase.ejecutar(new ActualizarUsuarioCommand(id, request));
    }

    @Override
    public UsuarioResponse obtenerPorUsername(String username) {
        return usuarioQueryService.obtenerPorUsername(username);
    }

    @Override
    public void eliminar(Long id) {
        eliminarUsuarioUseCase.ejecutar(new EliminarUsuarioCommand(id));
    }

    @Override
    public void cambiarPassword(CambiarPasswordRequest request) {
        cambiarPasswordUseCase.ejecutar(new CambiarPasswordCommand(request));
    }
}


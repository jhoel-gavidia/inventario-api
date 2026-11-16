package com.motorepuestos.inventario.categoria.service;

import com.motorepuestos.inventario.categoria.application.command.ActualizarCategoriaCommand;
import com.motorepuestos.inventario.categoria.application.command.CrearCategoriaCommand;
import com.motorepuestos.inventario.categoria.application.command.EliminarCategoriaCommand;
import com.motorepuestos.inventario.categoria.application.usecase.ActualizarCategoriaUseCase;
import com.motorepuestos.inventario.categoria.application.usecase.CrearCategoriaUseCase;
import com.motorepuestos.inventario.categoria.application.usecase.EliminarCategoriaUseCase;
import com.motorepuestos.inventario.categoria.dto.CategoriaRequest;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;
import com.motorepuestos.inventario.categoria.service.query.CategoriaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CrearCategoriaUseCase crearCategoriaUseCase;
    private final ActualizarCategoriaUseCase actualizarCategoriaUseCase;
    private final EliminarCategoriaUseCase eliminarCategoriaUseCase;
    private final CategoriaQueryService categoriaQueryService;

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        return crearCategoriaUseCase.ejecutar(new CrearCategoriaCommand(request));
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        return categoriaQueryService.obtenerPorId(id);
    }

    @Override
    public List<CategoriaResponse> obtenerTodos(String search, Boolean estado) {
        return categoriaQueryService.obtenerTodos(search, estado);
    }

    @Override
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        return actualizarCategoriaUseCase.ejecutar(new ActualizarCategoriaCommand(id, request));
    }

    @Override
    public void eliminar(Long id) {
        eliminarCategoriaUseCase.ejecutar(new EliminarCategoriaCommand(id));
    }
}

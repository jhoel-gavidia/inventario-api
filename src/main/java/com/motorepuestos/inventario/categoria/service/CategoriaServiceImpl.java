package com.motorepuestos.inventario.categoria.service;

import com.motorepuestos.inventario.categoria.application.command.ActualizarCategoriaCommand;
import com.motorepuestos.inventario.categoria.application.command.CrearCategoriaCommand;
import com.motorepuestos.inventario.categoria.application.command.EliminarCategoriaCommand;
import com.motorepuestos.inventario.categoria.application.usecase.ActualizarCategoriaUseCase;
import com.motorepuestos.inventario.categoria.application.usecase.CrearCategoriaUseCase;
import com.motorepuestos.inventario.categoria.application.usecase.EliminarCategoriaUseCase;
import com.motorepuestos.inventario.categoria.dto.CategoriaRequest;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;
import com.motorepuestos.inventario.categoria.mapper.CategoriaMapper;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaConsultaService categoriaConsultaService;
    private final CategoriaMapper categoriaMapper;
    private final CrearCategoriaUseCase crearCategoriaUseCase;
    private final ActualizarCategoriaUseCase actualizarCategoriaUseCase;
    private final EliminarCategoriaUseCase eliminarCategoriaUseCase;

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        return crearCategoriaUseCase.ejecutar(new CrearCategoriaCommand(request));
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        return categoriaMapper.toResponse(categoriaConsultaService.obtenerActiva(id));
    }

    @Override
    public List<CategoriaResponse> obtenerTodos() {
        return categoriaRepository.findByEstadoTrueOrderByNombreAsc()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
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

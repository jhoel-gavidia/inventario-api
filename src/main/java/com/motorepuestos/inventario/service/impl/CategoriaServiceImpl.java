package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.CategoriaRequest;
import com.motorepuestos.inventario.DTOs.Response.CategoriaResponse;
import com.motorepuestos.inventario.entity.Categoria;
import com.motorepuestos.inventario.exception.BusinessException;
import com.motorepuestos.inventario.exception.ResourceConflictException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.CategoriaMapper;
import com.motorepuestos.inventario.repository.CategoriaRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.service.AuditoriaService;
import com.motorepuestos.inventario.service.CategoriaService;
import com.motorepuestos.inventario.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final ProductoRepository productoRepository;
    private final AuditoriaService auditoriaService;
    private final JsonUtil jsonUtil;


    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        validarNombreDisponible(request.getNombre());

        Categoria categoria = categoriaMapper.toEntity(request);

        Categoria guardarCategoria = categoriaRepository.save(categoria);

        CategoriaResponse response = categoriaMapper.toResponse(guardarCategoria);

        auditoriaService.registrar(
                "CREAR",
                "CATEGORIA",
                guardarCategoria.getId(),
                null,
                jsonUtil.convertir(response)
        );

        return response;
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        return categoriaMapper.toResponse(obtenerCategoriaOrThrow(id));
    }

    @Override
    public List<CategoriaResponse> obtenerTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = obtenerCategoriaOrThrow(id);

        CategoriaResponse datosAntResponse = categoriaMapper.toResponse(categoria);

        boolean cambioNombre = !categoria.getNombre().equals(request.getNombre());

        if (cambioNombre) {
            validarNombreDisponible(request.getNombre());
        }

        categoria.setNombre(request.getNombre());

        CategoriaResponse datosNewResponse = categoriaMapper.toResponse(categoria);

        auditoriaService.registrar(
                "ACTUALIZAR",
                "CATEGORIA",
                categoria.getId(),
                jsonUtil.convertir(datosAntResponse),
                jsonUtil.convertir(datosNewResponse)
        );

        return datosNewResponse;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = obtenerCategoriaOrThrow(id);

        if (productoRepository.existsByCategoriaId(id)) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene productos asociados");
        }

        CategoriaResponse datosAntResponse = categoriaMapper.toResponse(categoria);

        categoriaRepository.delete(categoria);

        auditoriaService.registrar(
                "ELIMINAR",
                "CATEGORIA",
                categoria.getId(),
                jsonUtil.convertir(datosAntResponse),
                null
        );
    }

    private Categoria obtenerCategoriaOrThrow(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    private void validarNombreDisponible(String nombre) {
        if (categoriaRepository.existsByNombre(nombre)) {
            throw new ResourceConflictException("El nombre de la categoría ya existe");
        }
    }
}

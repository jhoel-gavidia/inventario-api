package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.CategoriaRequest;
import com.motorepuestos.inventario.DTOs.Response.CategoriaResponse;
import com.motorepuestos.inventario.entity.Categoria;
import com.motorepuestos.inventario.mapper.CategoriaMapper;
import com.motorepuestos.inventario.repository.CategoriaRepository;
import com.motorepuestos.inventario.service.CategoriaService;
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


    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = categoriaMapper.toEntity(request);

        Categoria guardarCategoria = categoriaRepository.save(categoria);

        return categoriaMapper.toResponse(guardarCategoria);
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return categoriaMapper.toResponse(categoria);
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

        categoria.setNombre(request.getNombre());

        return categoriaMapper.toResponse(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = obtenerCategoriaOrThrow(id);

        categoriaRepository.delete(categoria);
    }

    private Categoria obtenerCategoriaOrThrow(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
    }
}

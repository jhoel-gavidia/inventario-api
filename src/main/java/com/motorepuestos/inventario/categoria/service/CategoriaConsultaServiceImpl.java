package com.motorepuestos.inventario.categoria.service;

import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaConsultaServiceImpl implements CategoriaConsultaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public Categoria obtenerActiva(Long id) {
        return categoriaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }
}
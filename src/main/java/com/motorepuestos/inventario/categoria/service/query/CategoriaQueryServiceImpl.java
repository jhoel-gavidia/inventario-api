package com.motorepuestos.inventario.categoria.service.query;

import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;
import com.motorepuestos.inventario.categoria.mapper.CategoriaMapper;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import com.motorepuestos.inventario.categoria.service.CategoriaConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaQueryServiceImpl implements CategoriaQueryService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaConsultaService categoriaConsultaService;
    private final CategoriaMapper categoriaMapper;

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
}

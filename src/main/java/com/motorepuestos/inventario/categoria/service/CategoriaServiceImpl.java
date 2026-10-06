package com.motorepuestos.inventario.categoria.service;

import com.motorepuestos.inventario.categoria.dto.CategoriaRequest;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.categoria.mapper.CategoriaMapper;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import com.motorepuestos.inventario.auditoria.service.AuditoriaService;
import com.motorepuestos.inventario.common.util.JsonUtil;
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
        categoria.setEstado(true);

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
        return categoriaRepository.findByEstadoTrueOrderByNombreAsc()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = obtenerCategoriaConLockOrThrow(id);

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
        Categoria categoria = obtenerCategoriaConLockOrThrow(id);

        if (productoRepository.existsByCategoriaIdAndEstadoTrue(id)) {
            throw new BusinessException(
                    "No se puede eliminar la categoría porque tiene productos activos asociados"
            );
        }

        CategoriaResponse datosAntResponse = categoriaMapper.toResponse(categoria);

        categoria.setEstado(false);

        CategoriaResponse datosNewResponse = categoriaMapper.toResponse(categoria);

        auditoriaService.registrar(
                "ELIMINAR",
                "CATEGORIA",
                categoria.getId(),
                jsonUtil.convertir(datosAntResponse),
                jsonUtil.convertir(datosNewResponse)
        );
    }

    private Categoria obtenerCategoriaOrThrow(Long id) {
        return categoriaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    private Categoria obtenerCategoriaConLockOrThrow(Long id) {
        return categoriaRepository.findByIdForUpdate(id)
                .filter(categoria -> Boolean.TRUE.equals(categoria.getEstado()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    private void validarNombreDisponible(String nombre) {
        if (categoriaRepository.existsByNombreAndEstadoTrue(nombre)) {
            throw new ResourceConflictException("El nombre de la categoría ya existe");
        }
    }
}

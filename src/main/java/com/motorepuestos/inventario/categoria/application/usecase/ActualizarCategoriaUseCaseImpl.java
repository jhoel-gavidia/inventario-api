package com.motorepuestos.inventario.categoria.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.categoria.application.command.ActualizarCategoriaCommand;
import com.motorepuestos.inventario.categoria.domain.policy.CategoriaNombrePolicy;
import com.motorepuestos.inventario.categoria.domain.valueobject.NombreCategoria;
import com.motorepuestos.inventario.categoria.dto.CategoriaRequest;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.mapper.CategoriaMapper;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarCategoriaUseCaseImpl implements ActualizarCategoriaUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final AuditoriaHelper auditoriaHelper;
    private final CategoriaNombrePolicy categoriaNombrePolicy;

    @Override
    @Transactional
    public CategoriaResponse ejecutar(ActualizarCategoriaCommand command) {
        CategoriaRequest request = command.request();
        Categoria categoria = obtenerCategoriaConLockOrThrow(command.id());

        NombreCategoria nombre = categoriaNombrePolicy.validarYNormalizar(request.getNombre());
        categoriaNombrePolicy.validarDisponibleParaActualizacion(categoria, nombre);

        auditoriaHelper.registrarCambio(
                "ACTUALIZAR",
                "CATEGORIA",
                command.id(),
                categoria,
                categoriaMapper::toResponse,
                () -> categoria.setNombre(nombre.valor())
        );

        return categoriaMapper.toResponse(categoria);
    }

    private Categoria obtenerCategoriaConLockOrThrow(Long id) {
        return categoriaRepository.findByIdForUpdate(id)
                .filter(categoria -> Boolean.TRUE.equals(categoria.getEstado()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }
}

package com.motorepuestos.inventario.categoria.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.categoria.application.command.EliminarCategoriaCommand;
import com.motorepuestos.inventario.categoria.domain.policy.CategoriaEliminacionPolicy;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.mapper.CategoriaMapper;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarCategoriaUseCaseImpl implements EliminarCategoriaUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final CategoriaEliminacionPolicy categoriaEliminacionPolicy;
    private final AuditoriaHelper auditoriaHelper;

    @Override
    @Transactional
    public void ejecutar(EliminarCategoriaCommand command) {
        Categoria categoria = obtenerCategoriaConLockOrThrow(command.id());

        categoriaEliminacionPolicy.validarEliminacion(command.id());

        auditoriaHelper.registrarCambio(
                "ELIMINAR",
                "CATEGORIA",
                command.id(),
                categoria,
                categoriaMapper::toResponse,
                () -> categoria.setEstado(false)
        );
    }

    private Categoria obtenerCategoriaConLockOrThrow(Long id) {
        return categoriaRepository.findByIdForUpdate(id)
                .filter(categoria -> Boolean.TRUE.equals(categoria.getEstado()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }
}

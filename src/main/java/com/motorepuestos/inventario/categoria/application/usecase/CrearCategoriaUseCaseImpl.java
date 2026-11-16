package com.motorepuestos.inventario.categoria.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.categoria.application.command.CrearCategoriaCommand;
import com.motorepuestos.inventario.categoria.domain.policy.CategoriaNombrePolicy;
import com.motorepuestos.inventario.categoria.domain.valueobject.NombreCategoria;
import com.motorepuestos.inventario.categoria.dto.CategoriaRequest;
import com.motorepuestos.inventario.categoria.dto.CategoriaResponse;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.mapper.CategoriaMapper;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearCategoriaUseCaseImpl implements CrearCategoriaUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final AuditoriaHelper auditoriaHelper;
    private final CategoriaNombrePolicy categoriaNombrePolicy;

    @Override
    @Transactional
    public CategoriaResponse ejecutar(CrearCategoriaCommand command) {
        CategoriaRequest request = command.request();

        NombreCategoria nombre = categoriaNombrePolicy.validarYNormalizar(request.getNombre());
        categoriaNombrePolicy.validarDisponibleParaCreacion(nombre);

        Categoria categoria = categoriaMapper.toEntity(request);
        categoria.setNombre(nombre.valor());
        categoria.setEstado(true);

        Categoria guardarCategoria = categoriaRepository.save(categoria);
        CategoriaResponse response = categoriaMapper.toResponse(guardarCategoria);

        auditoriaHelper.registrarCreacion("CREAR", "CATEGORIA", guardarCategoria.getId(), response);
        return response;
    }
}

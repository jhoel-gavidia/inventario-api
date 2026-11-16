package com.motorepuestos.inventario.categoria.domain.policy;

import com.motorepuestos.inventario.categoria.domain.valueobject.NombreCategoria;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.repository.CategoriaRepository;
import com.motorepuestos.inventario.common.exception.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoriaNombrePolicy {

    private final CategoriaRepository categoriaRepository;

    public NombreCategoria validarYNormalizar(String nombre) {
        return NombreCategoria.de(nombre);
    }

    public void validarDisponibleParaCreacion(NombreCategoria nombre) {
        if (categoriaRepository.existsByNombreAndEstadoTrue(nombre.valor())) {
            throw new ResourceConflictException("El nombre de la categoría ya existe");
        }
    }

    public void validarDisponibleParaActualizacion(Categoria categoria, NombreCategoria nuevoNombre) {
        boolean cambioNombre = !categoria.getNombre().equals(nuevoNombre.valor());
        if (cambioNombre && categoriaRepository.existsByNombreAndEstadoTrue(nuevoNombre.valor())) {
            throw new ResourceConflictException("El nombre de la categoría ya existe");
        }
    }
}

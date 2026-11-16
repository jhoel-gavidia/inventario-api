package com.motorepuestos.inventario.categoria.domain.policy;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.producto.service.ProductoConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoriaEliminacionPolicy {

    private final ProductoConsultaService productoConsultaService;

    public void validarEliminacion(Long categoriaId) {
        if (productoConsultaService.existenActivosPorCategoria(categoriaId)) {
            throw new BusinessException(
                    "No se puede eliminar la categoría porque tiene productos activos asociados"
            );
        }
    }
}

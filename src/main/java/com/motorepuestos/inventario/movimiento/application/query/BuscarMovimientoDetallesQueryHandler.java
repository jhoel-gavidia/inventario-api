package com.motorepuestos.inventario.movimiento.application.query;

import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuscarMovimientoDetallesQueryHandler {

    private final MovimientoRepository movimientoRepository;

    public PaginaResponse<MovimientoDetalleView> handle(BuscarMovimientoDetallesQuery query) {
        String searchNormalizado = normalizarSearch(query.search());
        Tipo tipoEnum = parsearTipo(query.tipo());

        Page<MovimientoDetalleView> page = movimientoRepository.buscarDetalles(
                tipoEnum,
                searchNormalizado,
                query.pageable()
        );

        return PaginaResponse.from(page, view -> view);
    }

    private String normalizarSearch(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return "%" + search.trim().toLowerCase() + "%";
    }

    private Tipo parsearTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return null;
        }

        try {
            return Tipo.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Tipo de movimiento inválido: " + tipo);
        }
    }
}

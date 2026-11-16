package com.motorepuestos.inventario.movimiento.service.query;

import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.movimiento.application.query.BuscarMovimientoDetallesQuery;
import com.motorepuestos.inventario.movimiento.application.query.BuscarMovimientoDetallesQueryHandler;
import com.motorepuestos.inventario.movimiento.application.query.ListarMovimientosQuery;
import com.motorepuestos.inventario.movimiento.application.query.ListarMovimientosQueryHandler;
import com.motorepuestos.inventario.movimiento.application.query.ObtenerMovimientoPorIdQuery;
import com.motorepuestos.inventario.movimiento.application.query.ObtenerMovimientoPorIdQueryHandler;
import com.motorepuestos.inventario.movimiento.application.query.ObtenerResumenMovimientosHoyQuery;
import com.motorepuestos.inventario.movimiento.application.query.ObtenerResumenMovimientosHoyQueryHandler;
import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResumenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovimientoQueryServiceImpl implements MovimientoQueryService {

    private final ObtenerMovimientoPorIdQueryHandler obtenerMovimientoPorIdQueryHandler;
    private final ListarMovimientosQueryHandler listarMovimientosQueryHandler;
    private final BuscarMovimientoDetallesQueryHandler buscarMovimientoDetallesQueryHandler;
    private final ObtenerResumenMovimientosHoyQueryHandler obtenerResumenMovimientosHoyQueryHandler;

    @Override
    public MovimientoResponse obtenerPorId(Long id) {
        return obtenerMovimientoPorIdQueryHandler.handle(new ObtenerMovimientoPorIdQuery(id));
    }

    @Override
    public PaginaResponse<MovimientoResponse> obtenerTodos(Pageable pageable) {
        return listarMovimientosQueryHandler.handle(new ListarMovimientosQuery(pageable));
    }

    @Override
    public PaginaResponse<MovimientoDetalleView> buscarDetalles(
            String tipo,
            String search,
            Pageable pageable
    ) {
        return buscarMovimientoDetallesQueryHandler.handle(
                new BuscarMovimientoDetallesQuery(tipo, search, pageable)
        );
    }

    @Override
    public MovimientoResumenResponse resumenHoy() {
        return obtenerResumenMovimientosHoyQueryHandler.handle(new ObtenerResumenMovimientosHoyQuery());
    }
}

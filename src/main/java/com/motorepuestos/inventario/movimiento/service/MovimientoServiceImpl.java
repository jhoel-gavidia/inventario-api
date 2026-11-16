package com.motorepuestos.inventario.movimiento.service;

import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.application.usecase.RegistrarMovimientoUseCase;
import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.dto.MovimientoRequest;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResumenResponse;
import com.motorepuestos.inventario.movimiento.service.query.MovimientoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;
    private final MovimientoQueryService movimientoQueryService;

    @Override
    public MovimientoResponse obtenerPorId(Long id) {
        return movimientoQueryService.obtenerPorId(id);
    }

    @Override
    public MovimientoResponse registrar(MovimientoRequest request) {
        return registrarMovimientoUseCase.ejecutar(
                new RegistrarMovimientoCommand(request.getTipo(), request.getDetalles())
        );
    }

    @Override
    public PaginaResponse<MovimientoResponse> obtenerTodos(Pageable pageable) {
        return movimientoQueryService.obtenerTodos(pageable);
    }

    @Override
    public PaginaResponse<MovimientoDetalleView> buscarDetalles(
            String tipo,
            String search,
            Pageable pageable
    ) {
        return movimientoQueryService.buscarDetalles(tipo, search, pageable);
    }

    @Override
    public MovimientoResumenResponse resumenHoy() {
        return movimientoQueryService.resumenHoy();
    }
}

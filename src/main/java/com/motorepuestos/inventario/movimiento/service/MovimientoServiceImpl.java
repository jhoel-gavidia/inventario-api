package com.motorepuestos.inventario.movimiento.service;

import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.application.usecase.RegistrarMovimientoUseCase;
import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.dto.MovimientoRequest;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResumenResponse;
import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.movimiento.mapper.MovimientoMapper;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;

    @Override
    public MovimientoResponse obtenerPorId(Long id) {
        return movimientoMapper.toResponse(obtenerMovimientoOrThrow(id));
    }

    @Override
    public MovimientoResponse registrar(MovimientoRequest request) {
        return registrarMovimientoUseCase.ejecutar(
                new RegistrarMovimientoCommand(request.getTipo(), request.getDetalles())
        );
    }

    @Override
    public PaginaResponse<MovimientoResponse> obtenerTodos(Pageable pageable) {
        return PaginaResponse.from(
                movimientoRepository.findAllBy(pageable),
                movimientoMapper::toResponse
        );
    }

    @Override
    public PaginaResponse<MovimientoDetalleView> buscarDetalles(
            String tipo,
            String search,
            Pageable pageable
    ) {
        String searchNormalizado = normalizarSearch(search);
        Tipo tipoEnum = parsearTipo(tipo);

        Page<MovimientoDetalleView> page = tipoEnum == null
                ? movimientoRepository.buscarDetalles(searchNormalizado, pageable)
                : movimientoRepository.buscarDetallesPorTipo(
                        tipoEnum,
                        searchNormalizado,
                        pageable
                );

        return PaginaResponse.from(page, view -> view);
    }

    @Override
    public MovimientoResumenResponse resumenHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);

        long entradas = movimientoRepository.sumarCantidadPorTipoEntre(
                Tipo.ENTRADA,
                inicio,
                fin
        );

        long salidas = movimientoRepository.sumarCantidadPorTipoEntre(
                Tipo.SALIDA,
                inicio,
                fin
        );

        return new MovimientoResumenResponse(entradas, salidas);
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
            throw new BusinessException(
                    "Tipo de movimiento inválido: " + tipo
            );
        }
    }

    private Movimiento obtenerMovimientoOrThrow(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }
}

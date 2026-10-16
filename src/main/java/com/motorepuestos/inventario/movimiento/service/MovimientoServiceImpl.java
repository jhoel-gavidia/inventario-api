package com.motorepuestos.inventario.movimiento.service;

import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.dto.MovimientoRequest;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResumenResponse;
import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.movimiento.mapper.MovimientoMapper;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import com.motorepuestos.inventario.producto.service.ProductoStockService;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoStockService productoStockService;
    private final MovimientoMapper movimientoMapper;
    private final SecurityUtils securityUtils;
    private final AuditoriaHelper auditoriaHelper;

    @Override
    public MovimientoResponse obtenerPorId(Long id) {
        return movimientoMapper.toResponse(obtenerMovimientoOrThrow(id));
    }

    @Override
    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request) {
        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();

        List<DetalleMovimientoRequest> detallesOrdenados = ordenarDetalles(request.getDetalles());

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(request.getTipo());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuario);

        for (DetalleMovimientoRequest detalleRequest : detallesOrdenados) {
            DetalleMovimiento detalle = construirDetalle(movimiento, request.getTipo(), detalleRequest);
            movimiento.getDetalles().add(detalle);
        }

        Movimiento movimientoGuardado =
                movimientoRepository.save(movimiento);
        MovimientoResponse response = movimientoMapper.toResponse(movimientoGuardado);

        auditoriaHelper.registrarCreacion(
                "REGISTRAR",
                "MOVIMIENTO",
                movimientoGuardado.getId(),
                response
        );

        return response;
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

    private List<DetalleMovimientoRequest> ordenarDetalles(
            List<DetalleMovimientoRequest> detalles
    ) {

        validarProductosNoRepetidos(detalles);

        return detalles.stream()
                .sorted(Comparator.comparing(DetalleMovimientoRequest::getProductoId))
                .toList();
    }

    private void validarProductosNoRepetidos(
            List<DetalleMovimientoRequest> detalles
    ) {
        Set<Long> productos = new HashSet<>();

        for (DetalleMovimientoRequest detalle : detalles) {
            if (!productos.add(detalle.getProductoId())) {
                throw new BusinessException(
                        "El producto no puede repetirse dentro del mismo movimiento"
                );
            }
        }
    }

    private DetalleMovimiento construirDetalle(
            Movimiento movimiento, Tipo tipo, DetalleMovimientoRequest detalleRequest
    ) {
        Producto producto = productoStockService.obtenerBloqueadoParaMovimiento(
                detalleRequest.getProductoId()
        );
        Integer cantidad = validarCantidad(detalleRequest.getCantidad());

        actualizarStock(producto, tipo, cantidad);

        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);

        return detalle;
    }

    private Integer validarCantidad(Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }

        return cantidad;
    }

    private void actualizarStock(Producto producto, Tipo tipo, Integer cantidad) {

        if (tipo == null) {
            throw new BusinessException("El tipo de movimiento es obligatorio");
        }

        tipo.aplicar(producto, cantidad);
    }


    private Movimiento obtenerMovimientoOrThrow(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }
}

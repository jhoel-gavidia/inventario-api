package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.DetalleMovimientoRequest;
import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.DTOs.Response.PaginaResponse;
import com.motorepuestos.inventario.entity.*;
import com.motorepuestos.inventario.exception.BusinessException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.MovimientoMapper;
import com.motorepuestos.inventario.repository.MovimientoRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.security.SecurityUtils;
import com.motorepuestos.inventario.service.AuditoriaService;
import com.motorepuestos.inventario.service.MovimientoService;
import com.motorepuestos.inventario.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProductoRepository productoRepository;
    private final MovimientoMapper movimientoMapper;
    private final SecurityUtils securityUtils;
    private final AuditoriaService auditoriaService;
    private final JsonUtil jsonUtil;

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

        auditoriaService.registrar(
                "REGISTRAR",
                "MOVIMIENTO",
                movimientoGuardado.getId(),
                null,
                jsonUtil.convertir(response)
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
        Producto producto = obtenerProductoOrThrow(detalleRequest.getProductoId());
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

        int stockActual = producto.getStockActual();

        if (tipo == Tipo.ENTRADA) {

            if (cantidad > Integer.MAX_VALUE - stockActual) {
                throw new BusinessException(
                        "El stock del producto excedería el límite permitido: " + producto.getNombre()
                );
            }

            producto.setStockActual(stockActual + cantidad);

            return;
        }

        if (tipo != Tipo.SALIDA) {
            throw new BusinessException("El tipo de movimiento no es válido: " + tipo);
        }

        if (stockActual < cantidad) {
            throw new BusinessException(
                    "Stock insuficiente para el producto: " + producto.getNombre()
            );
        }

        producto.setStockActual(stockActual - cantidad);
    }


    private Movimiento obtenerMovimientoOrThrow(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }

    private Producto obtenerProductoOrThrow(Long id) {
        Producto producto = productoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw new BusinessException(
                    "El producto está inactivo y no admite movimientos: " + producto.getCodigo()
            );
        }

        return producto;
    }
}

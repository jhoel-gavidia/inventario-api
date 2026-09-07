package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.DetalleMovimientoRequest;
import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.entity.*;
import com.motorepuestos.inventario.exception.BusinessException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.mapper.MovimientoMapper;
import com.motorepuestos.inventario.repository.MovimientoRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.service.AuditoriaService;
import com.motorepuestos.inventario.service.MovimientoService;
import com.motorepuestos.inventario.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoMapper movimientoMapper;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final JsonUtil jsonUtil;

    @Override
    public MovimientoResponse obtenerPorId(Long id) {
        return movimientoMapper.toResponse(obtenerMovimientoOrThrow(id));
    }

    @Override
    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(request.getTipo());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuario);

        for (DetalleMovimientoRequest detalleRequest : request.getDetalles()) {
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
    public List<MovimientoResponse> obtenerTodos() {
        return movimientoRepository.findAll()
                .stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }

    private DetalleMovimiento construirDetalle(
            Movimiento movimiento, Tipo tipo, DetalleMovimientoRequest detalleRequest
    ) {
        Producto producto = obtenerProductoOrThrow(detalleRequest.getProductoId());
        Integer cantidad = detalleRequest.getCantidad();

        actualizarStock(producto, tipo, cantidad);

        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);

        return detalle;
    }

    private void actualizarStock(Producto producto, Tipo tipo, Integer cantidad) {
        if (tipo == Tipo.ENTRADA) {
            producto.setStockActual(
                    producto.getStockActual() + cantidad
            );

            return;
        }

        if (producto.getStockActual() < cantidad) {
            throw new BusinessException(
                    "Stock insuficiente para el producto: " + producto.getNombre()
            );
        }

        producto.setStockActual(producto.getStockActual() - cantidad);
    }


    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado en el contexto");
        }

        return usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private Movimiento obtenerMovimientoOrThrow(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }

    private Producto obtenerProductoOrThrow(Long id) {
        return productoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
}

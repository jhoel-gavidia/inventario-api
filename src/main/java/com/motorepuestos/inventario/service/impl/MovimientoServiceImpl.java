package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.DetalleMovimientoRequest;
import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.entity.*;
import com.motorepuestos.inventario.mapper.MovimientoMapper;
import com.motorepuestos.inventario.repository.MovimientoRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoMapper movimientoMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    public MovimientoResponse obtenerPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Movimiento no encontrado")
        );

        return movimientoMapper.toResponse(movimiento);
    }

    @Override
    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario autenticado no encontrado")
                );

        Movimiento movimiento = new Movimiento();

        movimiento.setTipo(request.getTipo());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuario);

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new RuntimeException("El movimiento debe tener al menos un detalle");
        }

        for(DetalleMovimientoRequest detalleRequest : request.getDetalles()){
            Producto producto = productoRepository.findById(
                    detalleRequest.getProductoId()
            ).orElseThrow(() ->
                    new RuntimeException("Producto no encontrado")
            );


            Integer cantidad = detalleRequest.getCantidad();

            if (request.getTipo() == Tipo.SALIDA
                    && producto.getStockActual() < cantidad) {

                throw new RuntimeException(
                        "Stock insuficiente para el producto: "
                                + producto.getNombre()
                );
            }

            if (request.getTipo() == Tipo.ENTRADA) {
                producto.setStockActual(
                        producto.getStockActual() + cantidad
                );
            } else {
                producto.setStockActual(
                        producto.getStockActual() - cantidad
                );
            }

            DetalleMovimiento detalle = new DetalleMovimiento();
            detalle.setMovimiento(movimiento);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);

            movimiento.getDetalles().add(detalle);
        }

        Movimiento movimientoGuardado =
                movimientoRepository.save(movimiento);

        return movimientoMapper.toResponse(movimientoGuardado);
    }

    @Override
    public List<MovimientoResponse> obtenerTodos() {
        return movimientoRepository.findAll()
                .stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }
}

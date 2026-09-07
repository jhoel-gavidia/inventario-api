package com.motorepuestos.inventario.service.impl;

import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.entity.Movimiento;
import com.motorepuestos.inventario.entity.Producto;
import com.motorepuestos.inventario.entity.Tipo;
import com.motorepuestos.inventario.mapper.MovimientoMapper;
import com.motorepuestos.inventario.repository.MovimientoRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.service.MovimientoService;
import lombok.RequiredArgsConstructor;
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
        Movimiento movimiento = new Movimiento();

        movimiento.setFecha(LocalDateTime.now());

        for(var detalle : movimiento.getDetalles()){
            Producto producto = productoRepository.findById(detalle.getProducto().getId()).orElseThrow(
                    () -> new RuntimeException("Producto no encontrado")
            );

            Integer cantidad = detalle.getCantidad();

            if(request.getTipo() == Tipo.ENTRADA) {
                producto.setStockActual(producto.getStockActual() + cantidad);
            } else {
                if(producto.getStockActual() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre()
                    );
                }

                producto.setStockActual(producto.getStockActual() - cantidad);
            }

            detalle.setProducto(producto);
            detalle.setMovimiento(movimiento);
            detalle.setCantidad(cantidad);
        }

        movimientoRepository.save(movimiento);

        return movimientoMapper.toResponse(movimiento);
    }

    @Override
    public List<MovimientoResponse> obtenerTodos() {
        return movimientoRepository.findAll()
                .stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }
}

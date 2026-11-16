package com.motorepuestos.inventario.movimiento.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.mapper.MovimientoMapper;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import com.motorepuestos.inventario.movimiento.service.strategy.MovimientoTipoStrategy;
import com.motorepuestos.inventario.movimiento.service.strategy.MovimientoTipoStrategyFactory;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.service.ProductoStockService;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrarMovimientoUseCaseImpl implements RegistrarMovimientoUseCase {

    private final MovimientoRepository movimientoRepository;
    private final ProductoStockService productoStockService;
    private final MovimientoMapper movimientoMapper;
    private final SecurityUtils securityUtils;
    private final AuditoriaHelper auditoriaHelper;
    private final MovimientoTipoStrategyFactory movimientoTipoStrategyFactory;

    @Override
    @Transactional
    public MovimientoResponse ejecutar(RegistrarMovimientoCommand command) {
        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();
        MovimientoTipoStrategy estrategia = movimientoTipoStrategyFactory.obtener(command.tipo());

        List<DetalleMovimientoRequest> detallesOrdenados = ordenarDetalles(command.detalles());

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(command.tipo());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuario);

        for (DetalleMovimientoRequest detalleRequest : detallesOrdenados) {
            DetalleMovimiento detalle = construirDetalle(movimiento, estrategia, detalleRequest);
            movimiento.getDetalles().add(detalle);
        }

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        MovimientoResponse response = movimientoMapper.toResponse(movimientoGuardado);

        auditoriaHelper.registrarCreacion(
                "REGISTRAR",
                "MOVIMIENTO",
                movimientoGuardado.getId(),
                response
        );

        return response;
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
            Movimiento movimiento,
            MovimientoTipoStrategy estrategia,
            DetalleMovimientoRequest detalleRequest
    ) {
        Producto producto = productoStockService.obtenerBloqueadoParaMovimiento(
                detalleRequest.getProductoId()
        );
        Integer cantidad = validarCantidad(detalleRequest.getCantidad());

        estrategia.aplicarStock(producto, cantidad);

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
}

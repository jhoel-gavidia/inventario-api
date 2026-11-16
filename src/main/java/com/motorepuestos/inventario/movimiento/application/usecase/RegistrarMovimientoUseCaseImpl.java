package com.motorepuestos.inventario.movimiento.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.domain.policy.MovimientoRegistroPolicy;
import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrarMovimientoUseCaseImpl implements RegistrarMovimientoUseCase {

    private final MovimientoRepository movimientoRepository;
    private final ProductoStockService productoStockService;
    private final MovimientoMapper movimientoMapper;
    private final SecurityUtils securityUtils;
    private final AuditoriaHelper auditoriaHelper;
    private final MovimientoTipoStrategyFactory movimientoTipoStrategyFactory;
    private final MovimientoRegistroPolicy movimientoRegistroPolicy;

    @Override
    @Transactional
    public MovimientoResponse ejecutar(RegistrarMovimientoCommand command) {
        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();
        MovimientoTipoStrategy estrategia = movimientoTipoStrategyFactory.obtener(command.tipo());

        List<DetalleMovimientoRequest> detallesOrdenados =
                movimientoRegistroPolicy.validarYOrdenarDetalles(command.detalles());

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

    private DetalleMovimiento construirDetalle(
            Movimiento movimiento,
            MovimientoTipoStrategy estrategia,
            DetalleMovimientoRequest detalleRequest
    ) {
        Producto producto = productoStockService.obtenerBloqueadoParaMovimiento(
                detalleRequest.getProductoId()
        );

        CantidadMovimiento cantidad = movimientoRegistroPolicy.validarCantidad(detalleRequest.getCantidad());

        estrategia.aplicarStock(producto, cantidad.valor());

        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad.valor());

        return detalle;
    }
}

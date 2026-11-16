package com.motorepuestos.inventario.movimiento.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.domain.factory.DetalleMovimientoFactory;
import com.motorepuestos.inventario.movimiento.domain.factory.MovimientoFactory;
import com.motorepuestos.inventario.movimiento.domain.policy.MovimientoRegistroPolicy;
import com.motorepuestos.inventario.movimiento.domain.valueobject.CantidadMovimiento;
import com.motorepuestos.inventario.movimiento.dto.DetalleMovimientoRequest;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.movimiento.mapper.MovimientoMapper;
import com.motorepuestos.inventario.movimiento.repository.MovimientoRepository;
import com.motorepuestos.inventario.movimiento.service.strategy.MovimientoTipoStrategy;
import com.motorepuestos.inventario.movimiento.service.strategy.MovimientoTipoStrategyFactory;
import com.motorepuestos.inventario.producto.domain.policy.StockBajoPolicy;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.service.ProductoStockService;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final MovimientoFactory movimientoFactory;
    private final DetalleMovimientoFactory detalleMovimientoFactory;
    private final StockBajoPolicy stockBajoPolicy;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MovimientoResponse ejecutar(RegistrarMovimientoCommand command) {
        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();
        MovimientoTipoStrategy estrategia = movimientoTipoStrategyFactory.obtener(command.tipo());

        List<DetalleMovimientoRequest> detallesOrdenados =
                movimientoRegistroPolicy.validarYOrdenarDetalles(command.detalles());

        List<DetalleMovimiento> detalles = detallesOrdenados.stream()
                .map(detalleRequest -> construirDetalle(estrategia, detalleRequest))
                .toList();

        Movimiento movimiento = movimientoFactory.crear(
                command.tipo(),
                usuario,
                LocalDateTime.now(),
                detalles
        );

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

        publicarAlertasDeStock(command.tipo(), detalles);

        MovimientoResponse response = movimientoMapper.toResponse(movimientoGuardado);

        auditoriaHelper.registrarCreacion(
                "REGISTRAR",
                "MOVIMIENTO",
                movimientoGuardado.getId(),
                response
        );

        return response;
    }

    private void publicarAlertasDeStock(Tipo tipo, List<DetalleMovimiento> detalles) {
        if (tipo != Tipo.SALIDA) {
            return;
        }

        detalles.stream()
                .map(DetalleMovimiento::getProducto)
                .map(stockBajoPolicy::evaluar)
                .forEach(alerta -> alerta.ifPresent(eventPublisher::publishEvent));
    }

    private DetalleMovimiento construirDetalle(
            MovimientoTipoStrategy estrategia,
            DetalleMovimientoRequest detalleRequest
    ) {
        Producto producto = productoStockService.obtenerBloqueadoParaMovimiento(
                detalleRequest.getProductoId()
        );

        CantidadMovimiento cantidad = movimientoRegistroPolicy.validarCantidad(detalleRequest.getCantidad());

        estrategia.aplicarStock(producto, cantidad.valor());

        return detalleMovimientoFactory.crear(producto, cantidad);
    }
}

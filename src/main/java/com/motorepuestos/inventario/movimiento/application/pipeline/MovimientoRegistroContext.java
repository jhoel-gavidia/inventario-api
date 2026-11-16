package com.motorepuestos.inventario.movimiento.application.pipeline;

import com.motorepuestos.inventario.movimiento.application.command.RegistrarMovimientoCommand;
import com.motorepuestos.inventario.movimiento.dto.MovimientoResponse;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.service.strategy.MovimientoTipoStrategy;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovimientoRegistroContext {

    private final RegistrarMovimientoCommand command;

    private Usuario usuario;
    private MovimientoTipoStrategy estrategia;
    private List<DetalleMovimiento> detalles;
    private Movimiento movimiento;
    private MovimientoResponse response;

    public MovimientoRegistroContext(RegistrarMovimientoCommand command) {
        this.command = command;
    }
}

package com.motorepuestos.inventario.movimiento.domain.factory;

import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.movimiento.entity.DetalleMovimiento;
import com.motorepuestos.inventario.movimiento.entity.Movimiento;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MovimientoFactory {

    public Movimiento crear(
            Tipo tipo,
            Usuario usuario,
            LocalDateTime fecha,
            List<DetalleMovimiento> detalles
    ) {
        if (tipo == null) {
            throw new BusinessException("El tipo de movimiento es obligatorio");
        }
        if (usuario == null) {
            throw new BusinessException("El usuario del movimiento es obligatorio");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new BusinessException("El movimiento debe tener al menos un detalle");
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(tipo);
        movimiento.setUsuario(usuario);
        movimiento.setFecha(fecha != null ? fecha : LocalDateTime.now());
        detalles.forEach(movimiento::agregarDetalle);

        return movimiento;
    }
}

package com.motorepuestos.inventario.movimiento.repository;

import com.motorepuestos.inventario.movimiento.dto.MovimientoDetalleView;
import com.motorepuestos.inventario.movimiento.entity.Tipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientoRepositoryCustom {

    Page<MovimientoDetalleView> buscarDetalles(Tipo tipo, String searchNormalizado, Pageable pageable);
}

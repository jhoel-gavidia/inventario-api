package com.motorepuestos.inventario.DTOs.Request;

import com.motorepuestos.inventario.entity.Tipo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequest {

    private Tipo tipo;

    private List<DetalleMovimientoRequest> detalles;


}

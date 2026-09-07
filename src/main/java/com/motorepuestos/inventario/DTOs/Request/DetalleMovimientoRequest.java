package com.motorepuestos.inventario.DTOs.Request;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleMovimientoRequest {

    private Long productoId;

    private Integer cantidad;
}

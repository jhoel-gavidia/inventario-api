package com.motorepuestos.inventario.DTOs.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleMovimientoRequest {

    @NotNull(message = "El id del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Max(value = 1000000, message = "La cantidad no puede superar 1_000_000")
    private Integer cantidad;
}
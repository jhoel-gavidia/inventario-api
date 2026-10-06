package com.motorepuestos.inventario.movimiento.dto;

import com.motorepuestos.inventario.movimiento.entity.Tipo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private Tipo tipo;

    @NotEmpty(message = "Debe incluir al menos un detalle de movimiento")
    @Valid
    private List<DetalleMovimientoRequest> detalles;
}
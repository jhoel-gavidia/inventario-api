package com.motorepuestos.inventario.DTOs.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CategoriaResponse {

    private final Long id;

    private final String nombre;

    private final Boolean estado;
}

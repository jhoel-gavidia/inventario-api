package com.motorepuestos.inventario.DTOs.Response;

import com.motorepuestos.inventario.entity.Rol;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class UsuarioResponse  {
    private final Long id;

    private final String username;

    private final Rol rol;

    private final Boolean estado;
}

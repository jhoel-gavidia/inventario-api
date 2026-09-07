package com.motorepuestos.inventario.DTOs.Request;

import com.motorepuestos.inventario.entity.Rol;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {

    private String username;

    private String password;

    private Rol rol;

    private Boolean estado;
}

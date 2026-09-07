package com.motorepuestos.inventario.controller;

import com.motorepuestos.inventario.DTOs.Request.UsuarioRequest;
import com.motorepuestos.inventario.DTOs.Request.UsuarioUpdateRequest;
import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;
import com.motorepuestos.inventario.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        log.info("Creando usuario");

        UsuarioResponse creado = usuarioService.crear(request);

        var location = uriBuilder
                .path("/api/v1/usuarios/{id}")
                .buildAndExpand(creado.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @PathVariable @Positive Long id
    ) {
        return ResponseEntity.ok(
                usuarioService.obtenerPorId(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(
                usuarioService.obtenerTodos()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {
        log.info("Actualizando usuario id={}", id);

        return ResponseEntity.ok(
                usuarioService.actualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Long id
    ) {
        log.info("Eliminando usuario id={}", id);

        usuarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
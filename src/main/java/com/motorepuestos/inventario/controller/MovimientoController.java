package com.motorepuestos.inventario.controller;

import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.service.MovimientoService;
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
@RequestMapping("/api/v1/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(
            @Valid @RequestBody MovimientoRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        log.info("Registrando movimiento de tipo {}", request.getTipo());

        MovimientoResponse registrado =
                movimientoService.registrar(request);

        var location = uriBuilder
                .path("/api/v1/movimientos/{id}")
                .buildAndExpand(registrado.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(registrado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponse> obtenerPorId(
            @PathVariable @Positive Long id
    ) {
        return ResponseEntity.ok(
                movimientoService.obtenerPorId(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> obtenerTodos() {
        return ResponseEntity.ok(
                movimientoService.obtenerTodos()
        );
    }
}
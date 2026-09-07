package com.motorepuestos.inventario.controller;

import com.motorepuestos.inventario.DTOs.Request.ProductoRequest;
import com.motorepuestos.inventario.DTOs.Response.ProductoResponse;
import com.motorepuestos.inventario.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(
            @Valid @RequestBody ProductoRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        log.info("Creando producto");

        ProductoResponse creado = productoService.crear(request);

        var location = uriBuilder
                .path("/api/v1/productos/{id}")
                .buildAndExpand(creado.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(
            @PathVariable @Positive Long id
    ) {
        return ResponseEntity.ok(
                productoService.obtenerPorId(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> obtenerTodos() {
        return ResponseEntity.ok(
                productoService.obtenerTodos()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ProductoRequest request
    ) {
        log.info("Actualizando producto id={}", id);

        return ResponseEntity.ok(
                productoService.actualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Long id
    ) {
        log.info("Eliminando producto id={}", id);

        productoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
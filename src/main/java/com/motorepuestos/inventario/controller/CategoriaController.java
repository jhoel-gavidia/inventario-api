package com.motorepuestos.inventario.controller;

import com.motorepuestos.inventario.DTOs.Request.CategoriaRequest;
import com.motorepuestos.inventario.DTOs.Response.CategoriaResponse;
import com.motorepuestos.inventario.service.CategoriaService;
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
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(
            @Valid @RequestBody CategoriaRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        log.info("Creando categoría");
        CategoriaResponse creada = categoriaService.crear(request);

        var location = uriBuilder
                .path("/api/v1/categorias/{id}")
                .buildAndExpand(creada.getId())
                .toUri();

        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerPorId(
            @PathVariable @Positive Long id
    ) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> obtenerTodos() {
        return ResponseEntity.ok(categoriaService.obtenerTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CategoriaRequest request
    ) {
        log.info("Actualizando categoría");
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        log.info("Eliminando categoría");
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
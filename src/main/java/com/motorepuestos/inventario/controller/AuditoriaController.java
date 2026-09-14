package com.motorepuestos.inventario.controller;

import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;
import com.motorepuestos.inventario.exception.BusinessException;
import com.motorepuestos.inventario.service.AuditoriaService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auditorias")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<AuditoriaResponse>> listar(
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) @Positive Long entidadId
    ) {
        log.info("Listando auditorías");

        if (entidad == null && entidadId == null) {
            return ResponseEntity.ok(auditoriaService.listar());
        }

        if (entidad == null || entidadId == null) {
            throw new BusinessException(
                    "Los parámetros 'entidad' y 'entidadId' deben enviarse juntos"
            );
        }

        return ResponseEntity.ok(
                auditoriaService.listarPorEntidad(entidad, entidadId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaResponse> obtenerPorId(
            @PathVariable @Positive Long id
    ) {
        log.info("Consultando auditoría id={}", id);

        return ResponseEntity.ok(
                auditoriaService.obtenerPorId(id)
        );
    }
}
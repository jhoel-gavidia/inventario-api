package com.motorepuestos.inventario.auditoria.controller;

import com.motorepuestos.inventario.auditoria.dto.AuditoriaResponse;
import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.auditoria.service.AuditoriaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auditorias")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<PaginaResponse<AuditoriaResponse>> listar(
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) @Positive Long entidadId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamano
    ) {
        log.info("Listando auditorías pagina={} tamano={}", pagina, tamano);

        Pageable pageable = PageRequest.of(pagina, tamano);

        if (entidad == null && entidadId == null) {
            return ResponseEntity.ok(auditoriaService.listar(pageable));
        }

        if (entidad == null || entidadId == null) {
            throw new BusinessException(
                    "Los parámetros 'entidad' y 'entidadId' deben enviarse juntos"
            );
        }

        return ResponseEntity.ok(
                auditoriaService.listarPorEntidad(entidad, entidadId, pageable)
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
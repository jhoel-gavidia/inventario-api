package com.motorepuestos.inventario.auditoria.controller;

import com.motorepuestos.inventario.auditoria.dto.AuditoriaResponse;
import com.motorepuestos.inventario.auditoria.dto.AuditoriaResumenResponse;
import com.motorepuestos.inventario.common.dto.PaginaResponse;
import com.motorepuestos.inventario.auditoria.service.AuditoriaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auditorias")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<PaginaResponse<AuditoriaResponse>> listar(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) @Positive Long entidadId,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(defaultValue = "0") @PositiveOrZero int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamano
    ) {
        log.info(
                "Listando auditorías search={} entidad={} accion={} desde={} pagina={} tamano={}",
                search,
                entidad,
                accion,
                desde,
                pagina,
                tamano
        );

        Pageable pageable = PageRequest.of(
                pagina,
                tamano,
                Sort.by(Sort.Direction.DESC, "fecha", "id")
        );

        return ResponseEntity.ok(
                auditoriaService.buscar(
                        search,
                        entidad,
                        entidadId,
                        accion,
                        desde,
                        pageable
                )
        );
    }

    @GetMapping("/resumen")
    public ResponseEntity<AuditoriaResumenResponse> resumen() {
        log.info("Consultando resumen de auditorías");

        return ResponseEntity.ok(auditoriaService.resumen());
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
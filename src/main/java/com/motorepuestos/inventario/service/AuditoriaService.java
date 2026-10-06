package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;
import com.motorepuestos.inventario.DTOs.Response.PaginaResponse;
import com.motorepuestos.inventario.entity.Usuario;
import org.springframework.data.domain.Pageable;

public interface AuditoriaService {

    void registrar(
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    );

    void registrar(
            Usuario usuario,
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    );

    PaginaResponse<AuditoriaResponse> listar(Pageable pageable);

    PaginaResponse<AuditoriaResponse> listarPorEntidad(
            String entidad,
            Long entidadId,
            Pageable pageable
    );

    AuditoriaResponse obtenerPorId(Long id);
}
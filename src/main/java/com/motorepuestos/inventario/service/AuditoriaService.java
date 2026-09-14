package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;

import java.util.List;

public interface AuditoriaService {

    void registrar(
            String accion,
            String entidad,
            Long entidadId,
            String datosAnt,
            String datosNew
    );

    List<AuditoriaResponse> listar();

    List<AuditoriaResponse> listarPorEntidad(String entidad, Long entidadId);

    AuditoriaResponse obtenerPorId(Long id);
}
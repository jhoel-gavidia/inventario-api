package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;
import com.motorepuestos.inventario.entity.Usuario;

import java.util.List;

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

    List<AuditoriaResponse> listar();

    List<AuditoriaResponse> listarPorEntidad(String entidad, Long entidadId);

    AuditoriaResponse obtenerPorId(Long id);
}
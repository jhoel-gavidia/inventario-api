package com.motorepuestos.inventario.categoria.service;

import com.motorepuestos.inventario.categoria.entity.Categoria;

public interface CategoriaConsultaService {

    Categoria obtenerActiva(Long id);
}
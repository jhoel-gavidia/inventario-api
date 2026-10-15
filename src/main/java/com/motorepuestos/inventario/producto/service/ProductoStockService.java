package com.motorepuestos.inventario.producto.service;

import com.motorepuestos.inventario.producto.entity.Producto;

public interface ProductoStockService {

    Producto obtenerBloqueadoParaMovimiento(Long id);
}

ALTER TABLE productos
    ADD CONSTRAINT chk_productos_stock_no_negativo
        CHECK (stock_actual >= 0);

ALTER TABLE detalles_movimientos
    ADD CONSTRAINT chk_detalles_cantidad_positiva
        CHECK (cantidad > 0);

CREATE INDEX idx_productos_categoria_id
    ON productos(categoria_id);

CREATE INDEX idx_detalles_movimiento_id
    ON detalles_movimientos(movimiento_id);

CREATE INDEX idx_detalles_producto_id
    ON detalles_movimientos(producto_id);

CREATE INDEX idx_auditorias_fecha
    ON auditorias(fecha DESC);

CREATE INDEX idx_movimientos_fecha
    ON movimientos(fecha DESC);

-- El UNIQUE global bloqueaba para siempre el nombre de lo soft-deleted.
-- Se reemplaza por un índice único parcial sobre las categorías activas.
ALTER TABLE categorias
    DROP CONSTRAINT categorias_nombre_key;

CREATE UNIQUE INDEX uq_categorias_nombre_activa
    ON categorias(nombre)
    WHERE estado = TRUE;

ALTER TABLE productos
    DROP CONSTRAINT productos_codigo_key;

CREATE UNIQUE INDEX uq_productos_codigo_activo
    ON productos(codigo)
    WHERE estado = TRUE;
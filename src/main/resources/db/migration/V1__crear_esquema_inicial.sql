CREATE TABLE usuarios (
                          usuario_id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          rol VARCHAR(255) NOT NULL,
                          estado BOOLEAN NOT NULL
);

CREATE TABLE categorias (
                            categoria_id BIGSERIAL PRIMARY KEY,
                            nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE productos (
                           producto_id BIGSERIAL PRIMARY KEY,
                           codigo VARCHAR(50) NOT NULL UNIQUE,
                           nombre VARCHAR(150) NOT NULL,
                           categoria_id BIGINT NOT NULL,
                           precio_compra NUMERIC(10, 2) NOT NULL,
                           precio_venta NUMERIC(10, 2) NOT NULL,
                           stock_actual INTEGER NOT NULL,
                           estado BOOLEAN NOT NULL,

                           CONSTRAINT fk_producto_categoria
                               FOREIGN KEY (categoria_id)
                                   REFERENCES categorias(categoria_id)
);

CREATE TABLE movimientos (
                             movimiento_id BIGSERIAL PRIMARY KEY,
                             tipo VARCHAR(255) NOT NULL,
                             fecha TIMESTAMP NOT NULL,
                             usuario_id BIGINT NOT NULL,

                             CONSTRAINT fk_movimiento_usuario
                                 FOREIGN KEY (usuario_id)
                                     REFERENCES usuarios(usuario_id)
);

CREATE TABLE detalles_movimientos (
                                      detalle_movimiento_id BIGSERIAL PRIMARY KEY,
                                      movimiento_id BIGINT NOT NULL,
                                      producto_id BIGINT NOT NULL,
                                      cantidad INTEGER NOT NULL,

                                      CONSTRAINT fk_detalle_movimiento
                                          FOREIGN KEY (movimiento_id)
                                              REFERENCES movimientos(movimiento_id),

                                      CONSTRAINT fk_detalle_producto
                                          FOREIGN KEY (producto_id)
                                              REFERENCES productos(producto_id)
);

CREATE TABLE auditorias (
                            auditoria_id BIGSERIAL PRIMARY KEY,
                            usuario_id BIGINT NOT NULL,
                            accion VARCHAR(50) NOT NULL,
                            entidad VARCHAR(50) NOT NULL,
                            entidad_id BIGINT NOT NULL,
                            datos_ant JSONB,
                            datos_new JSONB,
                            fecha TIMESTAMP NOT NULL,

                            CONSTRAINT fk_auditoria_usuario
                                FOREIGN KEY (usuario_id)
                                    REFERENCES usuarios(usuario_id)
);

CREATE INDEX idx_auditoria_entidad
    ON auditorias(entidad, entidad_id);
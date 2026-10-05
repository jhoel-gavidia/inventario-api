ALTER TABLE categorias
    ADD COLUMN estado BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE categorias
SET estado = TRUE
WHERE estado IS NULL;

CREATE INDEX idx_categorias_estado
    ON categorias(estado);
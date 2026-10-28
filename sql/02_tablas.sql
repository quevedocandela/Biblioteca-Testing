-- Ejecutar conectada a la base "biblioteca"
DROP TABLE IF EXISTS prestamo;
DROP TABLE IF EXISTS libro;
DROP TABLE IF EXISTS socio;

CREATE TABLE socio (
                       id            SERIAL PRIMARY KEY,
                       numero_socio  INTEGER NOT NULL UNIQUE,
                       dni           VARCHAR(15) NOT NULL UNIQUE,
                       nombre        VARCHAR(50) NOT NULL,
                       apellido      VARCHAR(50) NOT NULL,
                       activo        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE libro (
                       id                SERIAL PRIMARY KEY,
                       titulo            VARCHAR(150) NOT NULL,
                       autor             VARCHAR(100) NOT NULL,
                       editorial         VARCHAR(100),
                       anio_publicacion  INTEGER,
                       activo            BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE prestamo (
                          id                 SERIAL PRIMARY KEY,
                          socio_id           INTEGER NOT NULL REFERENCES socio(id),
                          libro_id           INTEGER NOT NULL REFERENCES libro(id),
                          fecha_prestamo     DATE NOT NULL DEFAULT CURRENT_DATE,
                          fecha_vencimiento  DATE NOT NULL,
                          fecha_devolucion   DATE
);

-- Un ejemplar no puede tener dos préstamos sin devolver
CREATE UNIQUE INDEX ux_libro_prestado
    ON prestamo(libro_id) WHERE fecha_devolucion IS NULL;
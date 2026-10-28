-- Ejecutar conectada a la base "biblioteca", después de 02_tablas.sql
INSERT INTO socio (numero_socio, dni, nombre, apellido, activo) VALUES
                                                                    (1001, '40111222', 'Lucía',  'Fernández', TRUE),
                                                                    (1002, '38555666', 'Martín', 'Gómez',     TRUE),
                                                                    (1003, '42777888', 'Sofía',  'Pereyra',   FALSE),
                                                                    (1004, '35999000', 'Diego',  'Romero',    TRUE);

INSERT INTO libro (titulo, autor, editorial, anio_publicacion, activo) VALUES
                                                                           ('Rayuela',              'Julio Cortázar',         'Sudamericana', 1963, TRUE),
                                                                           ('Rayuela',              'Julio Cortázar',         'Sudamericana', 1963, TRUE),
                                                                           ('Ficciones',            'Jorge Luis Borges',      'Emecé',        1944, TRUE),
                                                                           ('El túnel',             'Ernesto Sabato',         'Sur',          1948, TRUE),
                                                                           ('Cien años de soledad', 'Gabriel García Márquez', 'Sudamericana', 1967, TRUE),
                                                                           ('Martín Fierro',        'José Hernández',         'Losada',       1872, TRUE),
                                                                           ('Don Segundo Sombra',   'Ricardo Güiraldes',      'Losada',       1926, TRUE),
                                                                           ('El Aleph',             'Jorge Luis Borges',      'Emecé',        1949, FALSE);

INSERT INTO prestamo (socio_id, libro_id, fecha_prestamo, fecha_vencimiento, fecha_devolucion) VALUES
                                                                                                   (1, 1, CURRENT_DATE - 2,  CURRENT_DATE + 5,  NULL),
                                                                                                   (1, 3, CURRENT_DATE - 30, CURRENT_DATE - 23, CURRENT_DATE - 25),
                                                                                                   (2, 4, CURRENT_DATE - 12, CURRENT_DATE - 5,  NULL),
                                                                                                   (4, 5, CURRENT_DATE - 1,  CURRENT_DATE + 6,  NULL),
                                                                                                   (4, 6, CURRENT_DATE - 1,  CURRENT_DATE + 6,  NULL),
                                                                                                   (4, 7, CURRENT_DATE - 1,  CURRENT_DATE + 6,  NULL);
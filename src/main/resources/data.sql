INSERT INTO categorias (name)
VALUES ('SERIE');
INSERT INTO categorias (name)
VALUES ('PELICULA');
INSERT INTO categorias (name)
VALUES ('VIDEOJUEGO');
INSERT INTO categorias (name)
VALUES ('SUPERHEROE');
INSERT INTO categorias (name)
VALUES ('OTROS');


INSERT INTO funkos (nombre, precio, cantidad, ruta_imagen, fecha_creacion, fecha_actualizacion, categoria_id)
VALUES ('Funko 1', 10.0, 20, 'http://localhost:3000/storage/ancianos.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 1),
       ('Funko 2', 20.0, 12, 'http://localhost:3000/storage/ancianos2.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 2),
       ('Funko 3', 30.0, 23, 'http://localhost:3000/storage/monito.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 3),
       ('Funko 4', 40.0, 14, 'http://localhost:3000/storage/rajoy.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 4);


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
VALUES ('Iron Man Funko', 10.0, 20, 'http://localhost:3000/storage/ironMan.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 4),
       ('Merida Funko', 20.0, 12, 'http://localhost:3000/storage/merida.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 2),
       ('Spider Man Funko', 30.0, 20, 'http://localhost:3000/storage/spiderMan.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 4),
       ('Joey Friends Funko', 40.0, 14, 'http://localhost:3000/storage/joeyFriends.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 1),
       ('Mickey Mouse Funko', 50.0, 34, 'http://localhost:3000/storage/mickey.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 5),
       ('Fortnite Funko', 60.0, 25, 'http://localhost:3000/storage/fortnite.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 3);


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
VALUES ('Iron Man Funko', 10.0, 20, 'https://localhost:3000/storage/ironMan.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 4),
       ('Merida Funko', 20.0, 12, 'https://localhost:3000/storage/merida.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 2),
       ('Spider Man Funko', 30.0, 20, 'https://localhost:3000/storage/spiderMan.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 4),
       ('Joey Friends Funko', 40.0, 14, 'https://localhost:3000/storage/joeyFriends.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 1),
       ('Mickey Mouse Funko', 50.0, 34, 'https://localhost:3000/storage/mickey.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 5),
       ('Fortnite Funko', 60.0, 25, 'https://localhost:3000/storage/fortnite.png', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 3);



insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Eva', 'Gomez Uceda', 'admin', 'admin@gmail.net',
        '$2a$12$JTAHPo0Q34lOI4nAdC5KeuoxMlHYdTmbj/D90hsnQkKGJ7pfId6eC');

insert into USER_ROLES (user_id, roles)
values (1, 'ADMIN');


insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Eva', 'Gomez Uceda', 'user', 'user@gmail.net',
        '$2a$12$EuZd4fDtXTS2A2g9qaxeFuE4KQncLPn6.D.WoPsOSESewcslgIWqa');

insert into USER_ROLES (user_id, roles)
values (2, 'USER');


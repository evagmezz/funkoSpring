SELECT 'CREATE DATABASE funkos_tienda_api'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'funkos');

DROP TABLE IF EXISTS "funkos";
DROP SEQUENCE IF EXISTS funkos_id_seq;
DROP TABLE IF EXISTS "user_roles";
DROP TABLE IF EXISTS "usuarios";
DROP SEQUENCE IF EXISTS usuarios_id_seq;
DROP TABLE IF EXISTS "categorias";

CREATE SEQUENCE funkos_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2223372836854725607 START 6 CACHE 1;


CREATE TABLE "public", "funkos" (
    id bigint DEFAULT NEXTVAL('funkos_id_seq') NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    precio DOUBLE DEFAULT 0.0,
    cantidad INT DEFAULT 0,
    ruta_imagen TEXT DEFAULT 'imagen.png',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    categoria_id UUID
    CONSTRAINT funkos_pkey PRIMARY KEY (id),
    CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES funkos_tienda_api.categorias(id)
)WITH (
    OIDS = FALSE
);
INSERT INTO funkos_tienda_api.funkos (nombre, precio, cantidad, ruta_imagen, fecha_creacion, fecha_actualizacion, categoria_id)
VALUES ('Funko 1', 10.0, 10, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
         ('Funko 2', 20.0, 20, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
         ('Funko 3', 30.0, 30, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
         ('Funko 4', 40.0, 40, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
         ('Funko 5', 50.0, 50, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');


CREATE TABLE "public"."user_roles"
(
    "user_id" bigint NOT NULL,
    "roles"   character varying(255)
) WITH (oids = false);

INSERT INTO "user_roles" ("user_id", "roles")
VALUES (1, 'USER'),
       (1, 'ADMIN'),
       (2, 'USER')

CREATE SEQUENCE usuarios_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 3 CACHE 1;


CREATE TABLE "public", "usuarios" (
    id BIGINT DEFAULT NEXTVAL('usuarios_id_seq') NOT NULL,
    nombre CHARACTER VARYING(255) NOT NULL,
    apellidos CHARACTER VARYING(255) NOT NULL,
    username CHARACTER VARYING(255) NOT NULL,
    email CHARACTER VARYING(255) NOT NULL,
    password CHARACTER VARYING(255) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    isDeleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT usuarios_pkey PRIMARY KEY (id)
)with (
    OIDS = FALSE
);
INSERT INTO usuarios (nombre, apellidos, username, email, password, createdAt, updateAt, isDeleted)
VALUES ('Admin', 'Admin', 'admin', 'admin@email.org', 'admin1234', '2023-11-02 11:43:24.724871',
        '2023-11-02 11:43:24.724871', FALSE),
         ('User', 'User', 'user', 'user@email.org', 'user1234', '2023-11-22 11:43:34.724871',
          '2023-11-12 12:43:24.724871', FALSE);


CREATE TABLE "public", "categorias" (
    id bigint DEFAULT NEXTVAL('categorias_id_seq') NOT NULL,
    name CHARACTER VARYING(255) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    isDeleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT categorias_pkey PRIMARY KEY (id)
)with (
    OIDS = FALSE
);
INSERT INTO categorias (name, createdAt, updateAt, isDeleted)
VALUES ('Categoria 1', '2023-11-02 11:43:24.724871', '2023-11-02 11:43:24.724871', FALSE),
         ('Categoria 2', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE),
         ('Categoria 3', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE),
         ('Categoria 4', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE),
         ('Categoria 5', '2023-11-22 11:43:34.724871', '2023-11-12 12:43:24.724871', FALSE);


ALTER TABLE ONLY "public"."funkos"
    ADD CONSTRAINT "fk2fwq10nwymfv7fumctxt9vpgb" FOREIGN KEY (categoria_id) REFERENCES categorias (id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."user_roles"
    ADD CONSTRAINT "fk2chxp26bnpqjibydrikgq4t9e" FOREIGN KEY (user_id) REFERENCES usuarios (id) NOT DEFERRABLE;

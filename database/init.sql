SELECT 'CREATE DATABASE funkos_tienda_api'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'funkos');

DROP TABLE IF EXISTS "funkos";
DROP SEQUENCE IF EXISTS funkos_id_seq;
DROP TABLE IF EXISTS "user_roles";
DROP TABLE IF EXISTS "usuarios";
DROP SEQUENCE IF EXISTS usuarios_id_seq;
DROP TABLE IF EXISTS "categorias";

CREATE SEQUENCE funkos_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 6 CACHE 1;
CREATE TABLE "public"."funkos"
(
    id                  bigint           DEFAULT NEXTVAL('funkos_id_seq') NOT NULL,
    nombre              character varying(255)                            NOT NULL,
    precio              DOUBLE PRECISION DEFAULT '0.0',
    cantidad            integer          DEFAULT '0',
    ruta_imagen         TEXT             DEFAULT 'imagen.png',
    fecha_creacion      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP        NOT NULL,
    fecha_actualizacion TIMESTAMP        DEFAULT CURRENT_TIMESTAMP        NOT NULL,
    categoria_id        bigint                                            NOT NULL,
    CONSTRAINT funkos_pkey PRIMARY KEY ("id")
) WITH (
      OIDS = FALSE
    );
INSERT INTO funkos (id, nombre, precio, cantidad, ruta_imagen, fecha_creacion, fecha_actualizacion, categoria_id)
VALUES (1, 'Funko 1', 10.0, 10, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
       (2, 'Funko 2', 20.0, 20, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
       (3, 'Funko 3', 30.0, 30, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3),
       (4, 'Funko 4', 40.0, 40, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4),
       (5, 'Funko 5', 50.0, 50, 'imagen.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5);

CREATE TABLE "public"."user_roles"
(
    "user_id" bigint NOT NULL,
    "roles"   character varying(255)
) WITH (oids = false);

INSERT INTO "user_roles" ("user_id", "roles")
VALUES (1, 'USER'),
       (1, 'ADMIN'),
       (2, 'USER');

CREATE SEQUENCE usuarios_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 3 CACHE 1;

CREATE TABLE "public"."usuarios"
(
    id         BIGINT    DEFAULT NEXTVAL('usuarios_id_seq') NOT NULL,
    nombre     CHARACTER VARYING(255)                       NOT NULL,
    apellidos  CHARACTER VARYING(255)                       NOT NULL,
    username   CHARACTER VARYING(255)                       NOT NULL,
    email      CHARACTER VARYING(255)                       NOT NULL,
    password   CHARACTER VARYING(255)                       NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP          NOT NULL,
    is_deleted BOOLEAN   DEFAULT FALSE                      NOT NULL,
    CONSTRAINT usuarios_pkey PRIMARY KEY ("id")
) with (
      OIDS = FALSE
    );
INSERT INTO usuarios (id, nombre, apellidos, username, email, password, created_at, updated_at, is_deleted)
VALUES (1, 'Admin', 'Admin', 'admin', 'admin@email.org', '$2a$12$2ZLzptm/CZsCSFzUpRtYj.8LGq1GJLAlOp9UzqfI/X.xlschsHFoG', '2023-11-02 11:43:24.724871',
        '2023-11-02 11:43:24.724871', FALSE),
       (2, 'User', 'User', 'user', 'user@email.org', '$2a$12$R9S7o86keLRVe6B810HlHOc3YSVMFHulCgsCw9SGHJ9KSJ2qcl4DS', '2023-11-22 11:43:34.724871',
        '2023-11-12 12:43:24.724871', FALSE);

CREATE SEQUENCE categorias_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 6 CACHE 1;

CREATE TABLE "public"."categorias"
(
    id           bigint    DEFAULT NEXTVAL('categorias_id_seq') NOT NULL,
    name         CHARACTER VARYING(255)                         NOT NULL,
    "created_at" timestamp DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    "updated_at" timestamp DEFAULT CURRENT_TIMESTAMP            NOT NULL,
    is_deleted   BOOLEAN   DEFAULT FALSE                        NOT NULL,
    CONSTRAINT categorias_pkey PRIMARY KEY ("id")
) with (
      OIDS = FALSE
    );
INSERT INTO "categorias" ("id", "name", "created_at", "updated_at", "is_deleted")
VALUES (1, 'Categoria 1', '2023-11-02 11:43:24.724871', '2023-11-02 11:43:24.724871', FALSE),
       (2, 'Categoria 2', '2023-11-02 11:43:24.724871', '2023-11-02 11:43:24.724871', FALSE),
       (3, 'Categoria 3', '2023-11-02 11:43:24.724871', '2023-11-02 11:43:24.724871', FALSE),
       (4, 'Categoria 4', '2023-11-02 11:43:24.724871', '2023-11-02 11:43:24.724871', FALSE),
       (5, 'Categoria 5', '2023-11-02 11:43:24.724871', '2023-11-02 11:43:24.724871', FALSE);



ALTER TABLE ONLY "public"."funkos"
    ADD CONSTRAINT "fk2fwq10nwymfv7fumctxt9vpgb" FOREIGN KEY (categoria_id) REFERENCES categorias (id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."user_roles"
    ADD CONSTRAINT "fk2chxp26bnpqjibydrikgq4t9e" FOREIGN KEY (user_id) REFERENCES usuarios (id) NOT DEFERRABLE;


CREATE DATABASE IF NOT EXISTS reservas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE reservas;

CREATE TABLE IF NOT EXISTS usuario (
    id    VARCHAR(30)  NOT NULL,
    clave VARCHAR(100) NOT NULL,
    rol   VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'FUNCIONARIO'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS funcionario (
    id       VARCHAR(30)  NOT NULL,
    nombre   VARCHAR(100) NOT NULL,
    telefono VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_funcionario_usuario FOREIGN KEY (id) REFERENCES usuario (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS categoria (
    id          INT          NOT NULL AUTO_INCREMENT,
    descripcion VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS recurso (
    id           VARCHAR(30)  NOT NULL,
    id_categoria INT          NOT NULL,
    descripcion  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_recurso_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS reserva (
    id             INT          NOT NULL AUTO_INCREMENT,
    id_funcionario VARCHAR(30)  NOT NULL,
    actividad      VARCHAR(150) NOT NULL,
    fecha          DATE         NOT NULL,
    hora_inicio    TIME         NOT NULL,
    hora_fin       TIME         NOT NULL,
    estado         VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reserva_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id),
    CONSTRAINT ck_reserva_estado CHECK (estado IN ('ACTIVA', 'CANCELADA'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS reserva_recurso (
    id_reserva INT         NOT NULL,
    id_recurso VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_reserva, id_recurso),
    CONSTRAINT fk_reserva_recurso_reserva FOREIGN KEY (id_reserva) REFERENCES reserva (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reserva_recurso_recurso FOREIGN KEY (id_recurso) REFERENCES recurso (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS configuracion_ia (
    id      VARCHAR(30)  NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    modelo  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

INSERT IGNORE INTO usuario (id, clave, rol) VALUES
    ('admin', '123', 'ADMINISTRADOR'),
    ('111', '111', 'FUNCIONARIO'),
    ('222', '222', 'FUNCIONARIO'),
    ('123', '123', 'FUNCIONARIO');

INSERT IGNORE INTO funcionario (id, nombre, telefono) VALUES
    ('111', 'Juan Perez', '3323'),
    ('222', 'Maria Perez', '222222'),
    ('123', 'Joanfer Hidalgo', '0000-0000');

INSERT IGNORE INTO categoria (id, descripcion) VALUES
    (1, 'Sala para 10 personas'),
    (2, 'Laptop windows'),
    (3, 'Sala de Juntas');

INSERT IGNORE INTO recurso (id, id_categoria, descripcion) VALUES
    ('238715', 2, 'Laptop #238715'),
    ('34343', 1, 'Sala 1 primer piso'),
    ('45238', 2, 'Laptop #45238'),
    ('452784', 3, 'Sala de Juntas Principal');

INSERT IGNORE INTO reserva (id, id_funcionario, actividad, fecha, hora_inicio, hora_fin, estado) VALUES
    (1, '111', 'Reunion clientes', CURDATE() + INTERVAL 1 DAY, '08:00:00', '10:00:00', 'ACTIVA'),
    (2, '222', 'Charla tecnica', CURDATE() + INTERVAL 6 DAY, '08:00:00', '10:00:00', 'ACTIVA'),
    (3, '111', 'Sesion de Junta Directiva', CURDATE() + INTERVAL 6 DAY, '09:00:00', '11:00:00', 'ACTIVA'),
    (4, '111', 'Entrevista candidato', CURDATE() + INTERVAL 7 DAY, '10:00:00', '11:00:00', 'ACTIVA'),
    (5, '111', 'Reunion de Control', CURDATE() + INTERVAL 13 DAY, '08:00:00', '09:00:00', 'ACTIVA'),
    (6, '123', 'Trabajo', CURDATE() + INTERVAL 2 DAY, '07:00:00', '12:00:00', 'CANCELADA');

INSERT IGNORE INTO reserva_recurso (id_reserva, id_recurso) VALUES
    (1, '238715'),
    (1, '34343'),
    (2, '45238'),
    (3, '238715'),
    (3, '452784'),
    (4, '34343'),
    (5, '452784'),
    (6, '238715');

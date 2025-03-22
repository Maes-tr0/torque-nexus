CREATE SEQUENCE IF NOT EXISTS seq_role_id
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS seq_permission_id
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS roles
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS permissions
(
    id          BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS users
    ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT;

CREATE TABLE IF NOT EXISTS role_permission
(
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);
CREATE SEQUENCE IF NOT EXISTS seq_user_id
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users
(
    id              BIGINT                      NOT NULL,
    full_name       VARCHAR(255)                NOT NULL,
    email           VARCHAR(255)                NOT NULL UNIQUE,
    email_confirmed BOOLEAN                     NOT NULL,
    password        VARCHAR(255)                NOT NULL,
    phone_number    VARCHAR(15),
    role_id         BIGINT                      NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_user_email ON users (email);
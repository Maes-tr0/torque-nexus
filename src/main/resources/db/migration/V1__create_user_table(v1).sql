CREATE SEQUENCE IF NOT EXISTS seq_user_id START WITH 1 INCREMENT BY 1;

CREATE TABLE users
(
    id           BIGINT                      NOT NULL,
    full_name    VARCHAR(255)                NOT NULL,
    email        VARCHAR(255)                NOT NULL,
    password     VARCHAR(255)                NOT NULL,
    phone_number VARCHAR(15),
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);
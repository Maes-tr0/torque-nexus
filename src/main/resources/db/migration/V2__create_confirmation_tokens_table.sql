CREATE SEQUENCE IF NOT EXISTS seq_confirmation_token_id START WITH 1 INCREMENT BY 1;

CREATE TABLE confirmation_tokens
(
    id           BIGINT                      NOT NULL,
    token        VARCHAR(255) UNIQUE         NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expired_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITHOUT TIME ZONE,
    user_id      BIGINT                      NOT NULL,
    CONSTRAINT pk_confirmation_tokens PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_confirmation_token ON confirmation_tokens (token);
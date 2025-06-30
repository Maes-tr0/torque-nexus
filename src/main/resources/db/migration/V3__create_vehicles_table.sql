CREATE SEQUENCE IF NOT EXISTS seq_vehicle_id
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS vehicles
(
    id            BIGINT                      NOT NULL,
    vin_code      VARCHAR(17)                 NOT NULL UNIQUE,
    mark          VARCHAR(255)                NOT NULL,
    model         VARCHAR(255)                NOT NULL,
    year          INTEGER                     NOT NULL,
    license_plate VARCHAR(15)                 NOT NULL UNIQUE,
    user_id       BIGINT                      NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_vehicles PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS vehicles
    ADD CONSTRAINT fk_vehicle_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_vehicle_vin_code ON vehicles (vin_code);
CREATE INDEX IF NOT EXISTS idx_vehicle_user_id ON vehicles (user_id);
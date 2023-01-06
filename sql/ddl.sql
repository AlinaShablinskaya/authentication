DROP TABLE IF EXISTS Users CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL,
    email VARCHAR (65) NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    role VARCHAR DEFAULT 'USER' NOT NULL,
    status VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS verification_token
(
    id BIGSERIAL,
    token VARCHAR NOT NULL UNIQUE,
    user_id bigint NOT NULL,
    expiration_date timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users ON DELETE SET NULL
);
CREATE TABLE users (
    id bigint PRIMARY KEY,
    password_digest character varying(35) NOT NULL,
    username character varying(255) NOT NULL,
    registration_date date NOT NULL,
    active boolean NOT NULL
);
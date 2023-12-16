CREATE TABLE member
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    nickname   VARCHAR(255) NOT NULL UNIQUE,
    stream_key VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE member_channel
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL UNIQUE,
    streamer    VARCHAR(255) NOT NULL,
    on_air      BOOLEAN NOT NULL
);
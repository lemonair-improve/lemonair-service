CREATE TABLE member
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    login_id   VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(255) NOT NULL UNIQUE,
    stream_key VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE member_channel
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(255) NOT NULL UNIQUE,
    member_id       BIGINT       NOT NULL UNIQUE,
    total_streaming INTEGER      NOT NULL,
    started_at      TIMESTAMP,
    on_air          BOOLEAN      NOT NULL
);

CREATE TABLE donation
(
    id           SERIAL PRIMARY KEY AUTO_INCREMENT,
    streamer_id  BIGINT       NOT NULL,
    donator_id   BIGINT       NOT NULL,
    contents     VARCHAR(255) NOT NULL,
    donated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    donate_point INTEGER      NOT NULL
);

CREATE TABLE charge
(
    id  SERIAL PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    point INTEGER NOT NULL,
    charged_at TIMESTAMP NOT NULL DEFAULT NOW()
)

alter table donation change donated_at donated_at TIMESTAMP NOT NULL DEFAULT NOW();
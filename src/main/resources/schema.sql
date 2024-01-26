CREATE TABLE member
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    login_id   VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(255) NOT NULL UNIQUE,
    stream_key VARCHAR(255) NOT NULL UNIQUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    modified_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

ALTER TABLE member add created_at      TIMESTAMP    NOT NULL DEFAULT NOW();
ALTER TABLE member add modified_at      TIMESTAMP    NOT NULL DEFAULT NOW();

CREATE TABLE member_channel
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(255) NOT NULL UNIQUE,
    member_id       BIGINT       NOT NULL UNIQUE,
    total_streaming INTEGER      NOT NULL,
    started_at      TIMESTAMP,
    on_air          BOOLEAN      NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    modified_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

ALTER TABLE member_channel add created_at      TIMESTAMP    NOT NULL DEFAULT NOW();
ALTER TABLE member_channel add modified_at      TIMESTAMP    NOT NULL DEFAULT NOW();


CREATE TABLE point
(
    id         SERIAL PRIMARY KEY,
    member_id  BIGINT    NOT NULL,
    donator_id BIGINT,
    charge     BOOLEAN,
    point      INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
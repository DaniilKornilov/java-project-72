CREATE TABLE IF NOT EXISTS urls
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS url_checks
(
    id          BIGSERIAL PRIMARY KEY,
    url_id      BIGINT    NOT NULL,
    status_code INTEGER,
    h1          VARCHAR(255),
    title       VARCHAR(255),
    description VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_url
        FOREIGN KEY (url_id)
            REFERENCES urls (id)
);

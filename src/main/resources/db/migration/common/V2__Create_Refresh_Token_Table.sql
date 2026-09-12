CREATE TABLE refresh_token
(
    id         BIGINT AUTO_INCREMENT                              NOT NULL,
    user_id    BIGINT                                             NOT NULL,
    token      VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    expires_at TIMESTAMP                                          NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT fk_refresh_token_users_1 FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_active_refresh_token UNIQUE (token)
);
CREATE INDEX ix_refresh_token_expire ON refresh_token (expires_at);
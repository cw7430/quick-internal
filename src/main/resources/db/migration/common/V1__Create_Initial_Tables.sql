CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    auth_type  VARCHAR(10) NOT NULL DEFAULT 'NATIVE',
    nick_name  VARCHAR(15) NOT NULL,
    gender     CHAR(1)     NOT NULL,
    role       VARCHAR(10) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL     DEFAULT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT ck_users_gender CHECK (gender IN ('M', 'F')),
    CONSTRAINT ck_users_auth_type CHECK (auth_type IN ('NATIVE', 'SOCIAL', 'CROSS')),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'USER', 'LEFT')),
    CONSTRAINT ck_users_deleted_state CHECK (
        (role <> 'LEFT' AND deleted_at IS NULL)
            OR
        (role = 'LEFT' AND deleted_at IS NOT NULL)
        )
);

CREATE INDEX ix_active_users_created_at
    ON users (role, created_at DESC);

CREATE INDEX ix_delete_users_deleted_at
    ON users (role, deleted_at DESC);

CREATE TABLE native_users
(
    id            BIGINT                                             NOT NULL,
    email         VARCHAR(255)                                       NOT NULL,
    password_hash VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    CONSTRAINT pk_native_users PRIMARY KEY (id),
    CONSTRAINT fk_native_users_1 FOREIGN KEY (id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_native_users_email UNIQUE (email)
);

CREATE TABLE social_users
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    user_id          BIGINT                                                      NOT NULL,
    auth_provider    VARCHAR(20)                                                 NOT NULL,
    provider_user_id VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL,
    email            VARCHAR(255) NULL     DEFAULT NULL,
    CONSTRAINT pk_social_users PRIMARY KEY (id),
    CONSTRAINT fk_social_users_1 FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_user_provider UNIQUE (user_id, auth_provider),
    CONSTRAINT uq_social_provider_account UNIQUE (auth_provider, provider_user_id),
    CONSTRAINT ck_social_users_auth_provider CHECK (auth_provider IN ('NAVER', 'GOOGLE'))
);
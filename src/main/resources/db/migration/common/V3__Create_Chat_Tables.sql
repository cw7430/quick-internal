CREATE TABLE chat_room
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    valid      CHAR(1)               NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP             NULL     DEFAULT NULL,
    CONSTRAINT pk_chat_room PRIMARY KEY (id),
    CONSTRAINT ck_chat_room_valid CHECK ( valid IN ('Y', 'N') ),
    CONSTRAINT ck_chat_room_deleted_state CHECK (
        (valid = 'Y' AND deleted_at IS NULL)
            OR
        (valid = 'N' AND deleted_at IS NOT NULL)
        )
);

CREATE INDEX ix_active_chat_room ON chat_room (valid, updated_at DESC);
CREATE INDEX ix_delete_chat_room ON chat_room (valid, deleted_at DESC);

CREATE TABLE chat_member
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    chat_room_id BIGINT                NOT NULL,
    user_id      BIGINT                NOT NULL,
    accepted     CHAR(1)               NOT NULL DEFAULT 'N',
    CONSTRAINT pk_chat_member PRIMARY KEY (id),
    CONSTRAINT fk_chat_member_room_1 FOREIGN KEY (chat_room_id)
        REFERENCES chat_room (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_chat_member_users_1 FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_user_room UNIQUE (chat_room_id, user_id),
    CONSTRAINT ck_chat_member_accepted CHECK ( accepted IN ('Y', 'N') )
);

CREATE TABLE chat_message
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    chat_member_id BIGINT                NOT NULL,
    message        TEXT                  NOT NULL,
    valid          CHAR(1)               NOT NULL DEFAULT 'Y',
    unread         BIGINT                NOT NULL,
    created_at     TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP             NULL     DEFAULT NULL,
    CONSTRAINT pk_chat_message PRIMARY KEY (id),
    CONSTRAINT fk_chat_message_member_1 FOREIGN KEY (chat_member_id)
        REFERENCES chat_member (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_chat_message_valid CHECK ( valid IN ('Y', 'N') ),
    CONSTRAINT ck_chat_message_deleted_state CHECK (
        (valid = 'Y' AND deleted_at IS NULL)
            OR
        (valid = 'N' AND deleted_at IS NOT NULL)
        )
);

CREATE INDEX ix_active_chat_message ON chat_message (chat_member_id, valid, created_at DESC);
CREATE INDEX ix_delete_chat_message ON chat_message (chat_member_id, valid, deleted_at DESC);
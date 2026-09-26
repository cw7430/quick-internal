CREATE TABLE alarm
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    my_user_id  BIGINT                NOT NULL,
    `read`      CHAR(1)               NOT NULL DEFAULT 'N',
    valid       CHAR(1)               NOT NULL DEFAULT 'Y',
    created_at  TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP             NULL     DEFAULT NULL,
    active_flag TINYINT GENERATED ALWAYS AS (
        IF(valid = 'Y', 1, NULL)
        ) VIRTUAL,
    CONSTRAINT pk_alarm PRIMARY KEY (id),
    CONSTRAINT fk_alarm_users_1 FOREIGN KEY (my_user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_alarm_read CHECK (`read` IN ('Y', 'N')),
    CONSTRAINT ck_alarm_valid CHECK (valid IN ('Y', 'N')),
    CONSTRAINT ck_alarm_deleted_state CHECK (
        (valid = 'Y' AND deleted_at IS NULL)
            OR
        (valid = 'N' AND deleted_at IS NOT NULL)
        )
);
CREATE INDEX ix_active_alarm_user_created_at
    ON alarm (my_user_id, active_flag, created_at DESC, id DESC);

CREATE INDEX ix_deleted_alarm_created_at
    ON alarm (my_user_id, active_flag, deleted_at DESC, id DESC);

CREATE INDEX ix_alarm_user_unread
    ON alarm (my_user_id, active_flag, `read`);

CREATE TABLE chat_message_alarm
(
    id              BIGINT NOT NULL,
    chat_message_id BIGINT NOT NULL,
    chat_room_id    BIGINT NOT NULL,
    other_user_id   BIGINT NOT NULL,
    CONSTRAINT pk_chat_message_alarm PRIMARY KEY (id),
    CONSTRAINT fk_chat_message_alarm_1 FOREIGN KEY (id)
        REFERENCES alarm (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_chat_message_alarm_2 FOREIGN KEY (chat_message_id)
        REFERENCES chat_message (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_chat_message_alarm_room_1 FOREIGN KEY (chat_room_id)
        REFERENCES chat_room (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_chat_message_alarm_users_1 FOREIGN KEY (other_user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_chat_message_alarm_message_id UNIQUE (chat_message_id DESC)
);

CREATE TABLE chat_room_alarm
(
    id            BIGINT      NOT NULL,
    chat_room_id  BIGINT      NOT NULL,
    other_user_id BIGINT      NOT NULL,
    type          VARCHAR(20) NOT NULL,
    CONSTRAINT pk_chat_room_alarm PRIMARY KEY (id),
    CONSTRAINT fk_chat_room_alarm_1 FOREIGN KEY (id)
        REFERENCES alarm (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_chat_room_alarm_2 FOREIGN KEY (chat_room_id)
        REFERENCES chat_room (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_chat_room_alarm_users_1 FOREIGN KEY (other_user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_chat_room_alarm_type CHECK (type IN ('CREATE', 'ACCEPT'))
);

CREATE INDEX ix_chat_room_alarm
    ON chat_room_alarm (id DESC, chat_room_id DESC, other_user_id);
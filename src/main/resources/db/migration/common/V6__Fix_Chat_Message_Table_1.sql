ALTER TABLE chat_message
    ADD COLUMN active_flag TINYINT GENERATED ALWAYS AS (
        IF(valid = 'Y', 1, NULL)
        ) VIRTUAL;

DROP INDEX ix_active_chat_message ON chat_message;

CREATE INDEX ix_chat_message_active
    ON chat_message (active_flag, created_at DESC, id DESC, chat_member_id);
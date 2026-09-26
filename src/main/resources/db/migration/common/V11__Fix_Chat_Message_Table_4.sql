ALTER TABLE chat_message
    ADD COLUMN updated CHAR(1) NULL;

ALTER TABLE chat_message
    ADD COLUMN updated_message_at TIMESTAMP NULL DEFAULT NULL;

ALTER TABLE chat_message
    ADD CONSTRAINT ck_chat_message_updated
        CHECK ( updated IN ('Y', 'N') );

ALTER TABLE chat_message
    ADD CONSTRAINT ck_chat_message_updated_state
        CHECK (
            ((updated = 'N') AND (updated_message_at IS NULL))
                OR
            ((updated = 'Y') AND (updated_message_at IS NOT NULL))
            );

UPDATE chat_message
SET updated = IF(updated_message_at IS NULL, 'N', 'Y');

ALTER TABLE chat_message
    MODIFY COLUMN updated CHAR(1) NOT NULL DEFAULT 'N';

CREATE INDEX ix_all_chat_message
    ON chat_message (chat_room_id ASC, created_at DESC, id DESC);
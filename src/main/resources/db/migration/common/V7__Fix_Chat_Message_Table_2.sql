ALTER TABLE chat_message
    ADD COLUMN chat_room_id BIGINT NULL;

UPDATE chat_message cms
    JOIN chat_member cmb
    ON cms.chat_member_id = cmb.id
SET cms.chat_room_id = cmb.chat_room_id;

ALTER TABLE chat_message
    MODIFY COLUMN chat_room_id BIGINT NOT NULL;

ALTER TABLE chat_message
    ADD CONSTRAINT fk_chat_message_room_1
        FOREIGN KEY (chat_room_id)
            REFERENCES chat_room (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

DROP INDEX ix_chat_message_active ON chat_message;
DROP INDEX ix_delete_chat_message ON chat_message;

CREATE INDEX ix_active_chat_message
    ON chat_message (
                     chat_room_id,
                     active_flag,
                     created_at DESC,
                     id DESC
        );

CREATE INDEX ix_delete_chat_message
    ON chat_message (
                     chat_room_id,
                     active_flag,
                     deleted_at DESC,
                     id DESC
        );
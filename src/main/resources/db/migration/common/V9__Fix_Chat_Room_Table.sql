ALTER TABLE chat_room
    ADD COLUMN active_flag TINYINT GENERATED ALWAYS AS (
        IF(valid = 'Y', 1, NULL)
        ) VIRTUAL;

DROP INDEX ix_active_chat_room ON chat_room;
DROP INDEX ix_delete_chat_room ON chat_room;

CREATE INDEX ix_active_chat_room ON chat_room (id, active_flag, updated_at DESC);
CREATE INDEX ix_delete_chat_room ON chat_room (id, active_flag, deleted_at DESC);
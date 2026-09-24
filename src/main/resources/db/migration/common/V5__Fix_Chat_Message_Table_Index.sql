DROP INDEX ix_active_chat_message ON chat_message;

CREATE INDEX ix_active_chat_message
    ON chat_message (
                     chat_member_id,
                     valid,
                     created_at DESC,
                     id DESC
        );
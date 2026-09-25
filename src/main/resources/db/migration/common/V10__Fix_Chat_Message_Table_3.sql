CREATE INDEX ix_chat_message_member_active_unread
    ON chat_message (chat_member_id, active_flag, unread);

DROP INDEX ix_chat_message_member_unread ON chat_message;
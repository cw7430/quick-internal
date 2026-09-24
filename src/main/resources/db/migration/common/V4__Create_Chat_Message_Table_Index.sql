CREATE INDEX ix_chat_message_member_unread
    ON chat_message (chat_member_id, unread);
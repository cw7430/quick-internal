CREATE INDEX ix_chat_message_alarm_id
    ON chat_message_alarm (alarm_id, chat_message_id DESC, id DESC);

DROP INDEX ix_chat_message_alarm ON chat_message_alarm;
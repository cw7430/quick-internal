DROP INDEX ix_active_alarm_user_created_at ON alarm;
DROP INDEX ix_deleted_alarm_created_at ON alarm;
CREATE INDEX ix_active_alarm_user_created_at
    ON alarm (my_user_id, other_user_id, active_flag, created_at DESC, id DESC);
CREATE INDEX ix_deleted_alarm_created_at
    ON alarm (my_user_id, other_user_id, active_flag, deleted_at DESC, id DESC);
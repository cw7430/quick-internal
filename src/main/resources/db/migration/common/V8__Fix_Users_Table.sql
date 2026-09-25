ALTER TABLE users
    ADD COLUMN active_flag TINYINT GENERATED ALWAYS AS (
        IF(role <> 'LEFT', 1, NULL)
        ) VIRTUAL;

DROP INDEX ix_active_users_created_at ON users;
DROP INDEX ix_delete_users_deleted_at ON users;

CREATE INDEX ix_active_users_created_at
    ON users (id, active_flag, created_at DESC);

CREATE INDEX ix_delete_users_deleted_at
    ON users (id, active_flag, deleted_at DESC);
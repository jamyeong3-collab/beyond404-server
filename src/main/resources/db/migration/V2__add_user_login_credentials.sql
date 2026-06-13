ALTER TABLE users ADD COLUMN IF NOT EXISTS login_id VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS users_login_id_unique
    ON users (lower(login_id))
    WHERE login_id IS NOT NULL;

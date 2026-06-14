ALTER TABLE users ADD COLUMN IF NOT EXISTS firebase_uid VARCHAR(128);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(120);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS users_firebase_uid_unique
    ON users (firebase_uid)
    WHERE firebase_uid IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS users_email_unique
    ON users (lower(email))
    WHERE email IS NOT NULL;

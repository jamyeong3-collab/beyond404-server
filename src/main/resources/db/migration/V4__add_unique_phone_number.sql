CREATE UNIQUE INDEX IF NOT EXISTS users_phone_number_unique
    ON users (phone_number)
    WHERE phone_number IS NOT NULL AND phone_number <> '';

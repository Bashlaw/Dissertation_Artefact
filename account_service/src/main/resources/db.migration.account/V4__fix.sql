ALTER TABLE users
    ADD COLUMN IF NOT EXISTS dob VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS gender VARCHAR(255);

ALTER TABLE users
    ALTER COLUMN user_role_id TYPE bigint,
    ALTER COLUMN user_role_id DROP NOT NULL;
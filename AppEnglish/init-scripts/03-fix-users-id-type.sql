-- users.id is VARCHAR (e.g. "stu-001", UUID). Ensure child tables use VARCHAR for user_id
-- so the FK can be created. Run if you see: Key columns "user_id" and "id" are of incompatible types.

-- 1. Drop all foreign key constraints that reference users(id)
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN (
        SELECT c.conname AS fk_name,
               (SELECT relname FROM pg_class WHERE oid = c.conrelid) AS from_table
        FROM pg_constraint c
        WHERE c.contype = 'f'
          AND c.confrelid = 'public.users'::regclass
    ) LOOP
        EXECUTE format('ALTER TABLE public.%I DROP CONSTRAINT IF EXISTS %I', r.from_table, r.fk_name);
    END LOOP;
END $$;

-- 2. Alter user_id columns to VARCHAR in tables that reference users (so FK matches users.id)
-- Only run each line if that table/column exists and is currently bigint.
ALTER TABLE pronunciation_attempts
    ALTER COLUMN user_id TYPE VARCHAR(255) USING user_id::TEXT;

ALTER TABLE ai_conversations
    ALTER COLUMN user_id TYPE VARCHAR(255) USING user_id::TEXT;

ALTER TABLE ai_messages
    ALTER COLUMN user_id TYPE VARCHAR(255) USING user_id::TEXT;

ALTER TABLE face_verification_logs
    ALTER COLUMN user_id TYPE VARCHAR(255) USING user_id::TEXT;

ALTER TABLE xp_logs
    ALTER COLUMN user_id TYPE VARCHAR(255) USING user_id::TEXT;

ALTER TABLE user_profiles
    ALTER COLUMN user_id TYPE VARCHAR(255) USING user_id::TEXT;

-- Add more ALTERs for other tables that have user_id FK to users (e.g. notifications, test_attempts, etc.)
-- if they exist and fail with "incompatible types". Hibernate may create them as VARCHAR on next startup.

-- 3. Hibernate (ddl-auto=update) will recreate the foreign keys on next application startup.

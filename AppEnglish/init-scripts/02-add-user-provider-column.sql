-- Add provider column for AuthProvider enum (LOCAL, GOOGLE, FACEBOOK) if missing.
-- Run this if you see: column u1_0.provider does not exist.

-- PostgreSQL
ALTER TABLE users ADD COLUMN IF NOT EXISTS provider VARCHAR(50) DEFAULT 'LOCAL';
UPDATE users SET provider = 'LOCAL' WHERE provider IS NULL;
ALTER TABLE users ALTER COLUMN provider SET NOT NULL;

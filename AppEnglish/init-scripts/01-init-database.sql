-- English Learning Platform Database Initialization
-- This script sets up the initial database structure

-- Create database if it doesn't exist (handled by compose.yaml)
-- CREATE DATABASE IF NOT EXISTS english_learning;
-- USE english_learning;

-- Set character set and collation
ALTER DATABASE english_learning CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create application user (handled by compose.yaml environment variables)
-- GRANT ALL PRIVILEGES ON english_learning.* TO 'appuser'@'%';
-- FLUSH PRIVILEGES;

-- Insert roles (required for registration)
INSERT INTO role (name, created_at) VALUES
('ROLE_USER', NOW()),
('ROLE_ADMIN', NOW()),
('ROLE_TEACHER', NOW());

-- Add missing columns to users table (if they don't exist)
-- Using DO block for compatibility
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'level') THEN
        ALTER TABLE users ADD COLUMN level INTEGER DEFAULT 1;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'xp') THEN
        ALTER TABLE users ADD COLUMN xp INTEGER DEFAULT 0;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'streak') THEN
        ALTER TABLE users ADD COLUMN streak INTEGER DEFAULT 0;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'total_lessons_completed') THEN
        ALTER TABLE users ADD COLUMN total_lessons_completed INTEGER DEFAULT 0;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'face_id_enrolled') THEN
        ALTER TABLE users ADD COLUMN face_id_enrolled BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

-- Sample data for testing (optional - can be removed in production)
-- role_id: 1 = ROLE_USER, 2 = ROLE_ADMIN, 3 = ROLE_TEACHER
INSERT INTO users (id, email, password_hash, full_name, role_id, level, xp, streak, total_lessons_completed, face_id_enrolled, created_at, updated_at) VALUES
('usr-001', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Admin', 2, 10, 1000, 0, 0, FALSE, NOW(), NOW()),
('usr-002', 'teacher@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Teacher', 3, 5, 500, 0, 0, FALSE, NOW(), NOW()),
('usr-003', 'student@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Student', 1, 1, 0, 0, 0, FALSE, NOW(), NOW());

-- Insert user roles
INSERT INTO user_roles (user_id, role_name) VALUES
(1, 'ADMIN'),
(2, 'TEACHER'),
(3, 'STUDENT');

-- Sample topics
INSERT INTO topics (name, description, difficulty_level, order_index, estimated_duration_minutes, created_at) VALUES
('Basic Greetings', 'Learn common English greetings and introductions', 'BEGINNER', 1, 15, NOW()),
('Family Members', 'Vocabulary about family relationships', 'BEGINNER', 2, 20, NOW()),
('Food and Drinks', 'Essential food vocabulary', 'BEGINNER', 3, 25, NOW()),
('Present Simple', 'Grammar: Present Simple tense', 'INTERMEDIATE', 4, 30, NOW()),
('Past Tense', 'Grammar: Past Simple and Past Continuous', 'INTERMEDIATE', 5, 35, NOW());
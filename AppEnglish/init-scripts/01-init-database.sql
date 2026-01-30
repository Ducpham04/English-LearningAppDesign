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

-- Sample data for testing (optional - can be removed in production)
INSERT INTO users (email, password_hash, full_name, status, created_at) VALUES
('admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Admin', 'ACTIVE', NOW()),
('teacher@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Teacher', 'ACTIVE', NOW()),
('student@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Student', 'ACTIVE', NOW());

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
-- =====================================================
-- IMPORT USER AND USER-RELATED DATA
-- =====================================================
-- Run this AFTER running import_student_data.sql
-- This script creates a student user with progress data

-- =====================================================
-- 1. CREATE USER (Student)
-- =====================================================

-- Student User with specific ID for easy reference
-- role_id: 1 = ROLE_USER (Student)
INSERT INTO users (id, email, password_hash, full_name, avatar_url, role_id, level, xp, streak, total_lessons_completed, face_id_enrolled, created_at, updated_at)
VALUES 
    ('stu-001', 'student@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyen Van A', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user1', 1, 3, 1250, 7, 12, false, NOW(), NOW());

-- Teacher User
-- role_id: 2 = ROLE_TEACHER
INSERT INTO users (id, email, password_hash, full_name, avatar_url, role_id, level, xp, streak, total_lessons_completed, face_id_enrolled, created_at, updated_at)
VALUES 
    ('tea-001', 'teacher@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ms. Sarah Johnson', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Sarah', 2, 5, 5000, 30, 50, true, NOW(), NOW());

-- Admin User
-- role_id: 3 = ROLE_ADMIN
INSERT INTO users (id, email, password_hash, full_name, avatar_url, role_id, level, xp, streak, total_lessons_completed, face_id_enrolled, created_at, updated_at)
VALUES 
    ('adm-001', 'admin@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin', 3, 10, 10000, 100, 0, true, NOW(), NOW());

-- =====================================================
-- 2. ENROLL STUDENT TO CLASSES
-- =====================================================

INSERT INTO class_students (class_id, student_id, joined_at, is_face_verified)
VALUES 
    (1, 'stu-001', NOW(), false),
    (2, 'stu-001', NOW(), false);


-- =====================================================
-- 3. USER VOCABULARY PROGRESS
-- =====================================================

INSERT INTO user_vocabulary_progress (id, user_id, vocabulary_id, status, times_reviewed, correct_count, created_at, updated_at)
VALUES 
    (1, 'stu-001', 1, 'LEARNING', 2, 1, NOW(), NOW()),
    (2, 'stu-001', 2, 'LEARNING', 2, 2, NOW(), NOW()),
    (3, 'stu-001', 3, 'MASTERED', 5, 5, NOW(), NOW()),
    (4, 'stu-001', 4, 'NEW', 0, 0, NOW(), NOW()),
    (5, 'stu-001', 5, 'NEW', 0, 0, NOW(), NOW()),
    (6, 'stu-001', 6, 'LEARNING', 1, 0, NOW(), NOW()),
    (7, 'stu-001', 7, 'NEW', 0, 0, NOW(), NOW()),
    (8, 'stu-001', 8, 'NEW', 0, 0, NOW(), NOW()),
    (9, 'stu-001', 11, 'NEW', 0, 0, NOW(), NOW()),
    (10, 'stu-001', 12, 'NEW', 0, 0, NOW(), NOW());


-- =====================================================
-- 4. USER BADGES (Earned badges)
-- =====================================================

INSERT INTO user_badges (id, user_id, badge_id, earned_at, created_at, updated_at)
VALUES 
    (1, 'stu-001', 1, NOW(), NOW(), NOW()),  -- First Lesson badge
    (2, 'stu-001', 2, NOW(), NOW(), NOW());  -- 7 Day Streak badge


-- =====================================================
-- 5. XP HISTORY
-- =====================================================

INSERT INTO xp_logs (id, user_id, amount, reason, created_at)
VALUES 
    (1, 'stu-001', 50, 'Complete lesson: Cách phát âm tên riêng tiếng Anh', NOW()),
    (2, 'stu-001', 30, 'Complete speaking exercise', NOW()),
    (3, 'stu-001', 20, 'Learn 5 new vocabulary words', NOW()),
    (4, 'stu-001', 100, 'Complete test with score 90%', NOW()),
    (5, 'stu-001', 10, 'Daily login bonus', NOW()),
    (6, 'stu-001', 50, 'Complete lesson: Giới thiệu bản thân cơ bản', NOW()),
    (7, 'stu-001', 40, 'Complete lesson: Từ vựng về quốc tịch', NOW()),
    (8, 'stu-001', 60, 'Complete lesson: Cách chào hỏi lịch sự', NOW()),
    (9, 'stu-001', 35, 'Complete lesson: Các câu hỏi thông thường', NOW()),
    (10, 'stu-001', 25, 'Daily streak bonus (day 7)', NOW()),
    (11, 'stu-001', 50, 'Complete quiz: Giới thiệu bản thân', NOW()),
    (12, 'stu-001', 30, 'Practice vocabulary: 3 words', NOW());


-- =====================================================
-- 6. NOTIFICATIONS
-- =====================================================

INSERT INTO notifications (id, user_id, message, is_read, metadata, created_at, updated_at)
VALUES 
    (1, 'stu-001', 'Chào mừng bạn đến với English AI! Hãy bắt đầu bài học đầu tiên nào.', false, '{"type": "welcome"}', NOW(), NOW()),
    (2, 'stu-001', 'Bạn đã có streak 7 ngày! Tiếp tục duy trì nhé.', false, '{"type": "streak"}', NOW(), NOW()),
    (3, 'stu-001', 'Bài học mới đã được cập nhật: Business English', false, '{"type": "new_content", "course_id": 2}', NOW(), NOW()),
    (4, 'stu-001', 'Bạn đã hoàn thành chương "Giới thiệu bản thân"!', true, '{"type": "lesson_complete", "session_id": 1}', NOW(), NOW()),
    (5, 'stu-001', 'Chúc mừng! Bạn đã nhận huy hiệu "First Lesson"', true, '{"type": "badge_earned", "badge_id": 1}', NOW(), NOW());


-- =====================================================
-- 7. LESSON PROGRESS (Optional - if you have a lesson_progress table)
-- =====================================================
-- Note: If you have a lesson_progress table, add progress data here
-- For now, lesson completion is tracked via total_lessons_completed in users table


-- =====================================================
-- VERIFY DATA
-- =====================================================

SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'User Badges', COUNT(*) FROM user_badges WHERE user_id = 'stu-001'
UNION ALL
SELECT 'User Vocab Progress', COUNT(*) FROM user_vocabulary_progress WHERE user_id = 'stu-001'
UNION ALL
SELECT 'XP Logs', COUNT(*) FROM xp_logs WHERE user_id = 'stu-001'
UNION ALL
SELECT 'Notifications', COUNT(*) FROM notifications WHERE user_id = 'stu-001'
UNION ALL
SELECT 'Class Enrollments', COUNT(*) FROM class_students WHERE student_id = 'stu-001';


-- =====================================================
-- TEST LOGIN
-- =====================================================
-- After running this script, you can login with:
-- Email: student@demo.com
-- Password: password123

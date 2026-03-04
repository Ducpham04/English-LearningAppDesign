-- =====================================================
-- IMPORT DATA FOR STUDENT - ENGLISH LEARNING APP
-- =====================================================
-- Database: PostgreSQL (lms_database)
-- Run this AFTER tables are created by JPA

-- =====================================================
-- 1. ROLES (Reference table)
-- =====================================================

-- Note: JPA will create this table. Insert role data:
INSERT INTO role (name, created_at) VALUES 
    ('ROLE_STUDENT', NOW()),
    ('ROLE_TEACHER', NOW()),
    ('ROLE_ADMIN', NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 2. USERS
-- =====================================================

-- Teacher User (email: teacher@demo.com, password: password123)
INSERT INTO users (id, email, password_hash, full_name, avatar_url, role, level, xp, streak, total_lessons_completed, face_id_enrolled, created_at, updated_at)
VALUES 
    ('tea-001', 'teacher@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ms. Sarah Johnson', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Sarah', 'TEACHER', 5, 5000, 30, 50, true, NOW(), NOW());

-- Student User (email: student@demo.com, password: password123)
INSERT INTO users (id, email, password_hash, full_name, avatar_url, role, level, xp, streak, total_lessons_completed, face_id_enrolled, created_at, updated_at)
VALUES 
    ('stu-001', 'student@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyễn Văn A', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user1', 'STUDENT', 3, 1250, 7, 12, false, NOW(), NOW());

-- Admin User (email: admin@demo.com, password: password123)
INSERT INTO users (id, email, password_hash, full_name, avatar_url, role, level, xp, streak, total_lessons_completed, face_id_enrolled, created_at, updated_at)
VALUES 
    ('adm-001', 'admin@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin', 'ADMIN', 10, 10000, 100, 0, true, NOW(), NOW());

-- =====================================================
-- 3. CURRICULUM (COURSES)
-- =====================================================

INSERT INTO courses (id, title, description, thumbnail_url, price, teacher_id, created_at, updated_at)
VALUES 
    (1, 'English Conversation - Beginner', 
     'Khóa học giao tiếp tiếng Anh cơ bản dành cho người mới bắt đầu.',
     'https://images.unsplash.com/photo-1543269865-cbf427effbad?w=800', 
     0, 'tea-001', NOW(), NOW()),
    (2, 'Business English', 
     'Tiếng Anh chuyên ngành cho môi trường công sở.',
     'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800', 
     0, 'tea-001', NOW(), NOW()),
    (3, 'IELTS Speaking & Listening', 
     'Luyện thi IELTS Speaking và Listening.',
     'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800', 
     0, 'tea-001', NOW(), NOW());

-- =====================================================
-- 4. SESSIONS
-- =====================================================

INSERT INTO sessions (id, title, order_index, course_id, created_at, updated_at)
VALUES 
    (1, 'Giới thiệu bản thân', 1, 1, NOW(), NOW()),
    (2, 'Chào hỏi và xã giao', 2, 1, NOW(), NOW()),
    (3, 'Mua sắm và giao dịch', 3, 1, NOW(), NOW()),
    (4, 'Đặt đồ ăn và nhà hàng', 4, 1, NOW(), NOW()),
    (5, 'Hỏi đường và di chuyển', 5, 1, NOW(), NOW()),
    (6, 'Email viết tiếng Anh', 1, 2, NOW(), NOW()),
    (7, 'Cuộc họp trong tiếng Anh', 2, 2, NOW(), NOW()),
    (8, 'Thuyết trình chuyên nghiệp', 3, 2, NOW(), NOW()),
    (9, 'IELTS Speaking Part 1', 1, 3, NOW(), NOW()),
    (10, 'IELTS Speaking Part 2', 2, 3, NOW(), NOW()),
    (11, 'IELTS Speaking Part 3', 3, 3, NOW(), NOW()),
    (12, 'IELTS Listening Strategies', 4, 3, NOW(), NOW());

-- =====================================================
-- 5. LESSONS
-- =====================================================

INSERT INTO lessons (id, title, course_id, session_id, type, order_index, content_payload, created_at, updated_at)
VALUES 
    -- Session 1
    (1, 'Cách phát âm tên riêng tiếng Anh', 1, 1, 'VIDEO', 1, '{"duration": 300}', NOW(), NOW()),
    (2, 'Giới thiệu bản thân cơ bản', 1, 1, 'SPEAKING', 2, '{}', NOW(), NOW()),
    (3, 'Từ vựng về quốc tịch', 1, 1, 'VOCAB', 3, '{}', NOW(), NOW()),
    (4, 'Bài tập: Giới thiệu bản thân', 1, 1, 'QUIZ', 4, '{}', NOW(), NOW()),
    -- Session 2
    (5, 'Cách chào hỏi lịch sự', 1, 2, 'VIDEO', 1, '{"duration": 240}', NOW(), NOW()),
    (6, 'Các câu hỏi thông thường', 1, 2, 'SPEAKING', 2, '{}', NOW(), NOW()),
    (7, 'Từ vựng: Chào hỏi', 1, 2, 'VOCAB', 3, '{}', NOW(), NOW()),
    -- Session 3
    (8, 'Mua sắm tại cửa hàng', 1, 3, 'VIDEO', 1, '{"duration": 360}', NOW(), NOW()),
    (9, 'Hỏi giá và trả giá', 1, 3, 'SPEAKING', 2, '{}', NOW(), NOW()),
    (10, 'Từ vựng: Mua sắm', 1, 3, 'VOCAB', 3, '{}', NOW(), NOW()),
    -- Session 4
    (11, 'Đặt đồ ăn', 1, 4, 'VIDEO', 1, '{"duration": 280}', NOW(), NOW()),
    (12, 'Gọi món tại nhà hàng', 1, 4, 'SPEAKING', 2, '{}', NOW(), NOW()),
    -- Session 5
    (13, 'Hỏi đường', 1, 5, 'VIDEO', 1, '{"duration": 300}', NOW(), NOW()),
    (14, 'Hướng dẫn đường đi', 1, 5, 'SPEAKING', 2, '{}', NOW(), NOW()),
    -- Session 6
    (15, 'Cấu trúc email tiếng Anh', 2, 6, 'VIDEO', 1, '{"duration": 400}', NOW(), NOW()),
    (16, 'Cách viết email formal', 2, 6, 'SPEAKING', 2, '{}', NOW(), NOW()),
    -- Session 9
    (17, 'Giới thiệu IELTS Speaking', 3, 9, 'VIDEO', 1, '{"duration": 350}', NOW(), NOW()),
    (18, 'Các câu hỏi Part 1', 3, 9, 'SPEAKING', 2, '{}', NOW(), NOW()),
    (19, 'Luyện tập: IELTS Part 1', 3, 9, 'QUIZ', 3, '{}', NOW(), NOW());

-- =====================================================
-- 6. VOCABULARY
-- =====================================================

INSERT INTO vocabularies (id, word, phonetic, audio_url, meanings, tags, created_at, updated_at)
VALUES 
    (1, 'Vietnamese', '/ˌvjetnəˈmiːz/', NULL, '[{"pos": "noun", "def": "Người Việt Nam"}]', '["beginner"]', NOW(), NOW()),
    (2, 'student', '/ˈstjuːdənt/', NULL, '[{"pos": "noun", "def": "Học sinh"}]', '["beginner"]', NOW(), NOW()),
    (3, 'from', '/frɒm/', NULL, '[{"pos": "preposition", "def": "Từ"}]', '["beginner"]', NOW(), NOW()),
    (4, 'live', '/lɪv/', NULL, '[{"pos": "verb", "def": "Sống"}]', '["beginner"]', NOW(), NOW()),
    (5, 'age', '/eɪdʒ/', NULL, '[{"pos": "noun", "def": "Tuổi"}]', '["beginner"]', NOW(), NOW()),
    (6, 'hello', '/həˈləʊ/', NULL, '[{"pos": "interjection", "def": "Xin chào"}]', '["beginner"]', NOW(), NOW()),
    (7, 'nice to meet you', '/naɪs tuː miːt juː/', NULL, '[{"pos": "phrase", "def": "Rất vui được gặp"}]', '["beginner"]', NOW(), NOW()),
    (8, 'how are you', '/haʊ ɑːr juː/', NULL, '[{"pos": "phrase", "def": "Bạn khỏe không?"}]', '["beginner"]', NOW(), NOW()),
    (9, 'goodbye', '/ɡʊdˈbaɪ/', NULL, '[{"pos": "interjection", "def": "Tạm biệt"}]', '["beginner"]', NOW(), NOW()),
    (10, 'see you later', '/siː juː ˈleɪtər/', NULL, '[{"pos": "phrase", "def": "Gặp lại"}]', '["beginner"]', NOW(), NOW()),
    (11, 'how much', '/haʊ mʌtʃ/', NULL, '[{"pos": "phrase", "def": "Bao nhiêu tiền?"}]', '["beginner"]', NOW(), NOW()),
    (12, 'expensive', '/ɪkˈspensɪv/', NULL, '[{"pos": "adjective", "def": "Đắt tiền"}]', '["beginner"]', NOW(), NOW()),
    (13, 'cheap', '/tʃiːp/', NULL, '[{"pos": "adjective", "def": "Rẻ"}]', '["beginner"]', NOW(), NOW()),
    (14, 'discount', '/ˈdɪskaʊnt/', NULL, '[{"pos": "noun", "def": "Giảm giá"}]', '["beginner"]', NOW(), NOW()),
    (15, 'buy', '/baɪ/', NULL, '[{"pos": "verb", "def": "Mua"}]', '["beginner"]', NOW(), NOW()),
    (16, 'restaurant', '/ˈrestrɒnt/', NULL, '[{"pos": "noun", "def": "Nhà hàng"}]', '["beginner"]', NOW(), NOW()),
    (17, 'menu', '/ˈmenjuː/', NULL, '[{"pos": "noun", "def": "Thực đơn"}]', '["beginner"]', NOW(), NOW()),
    (18, 'bill', '/bɪl/', NULL, '[{"pos": "noun", "def": "Hóa đơn"}]', '["beginner"]', NOW(), NOW()),
    (19, 'delicious', '/dɪˈlɪʃəs/', NULL, '[{"pos": "adjective", "def": "Ngon"}]', '["beginner"]', NOW(), NOW()),
    (20, 'hungry', '/ˈhʌŋɡri/', NULL, '[{"pos": "adjective", "def": "Đói"}]', '["beginner"]', NOW(), NOW());

-- =====================================================
-- 7. TESTS
-- =====================================================

INSERT INTO tests (id, title, duration, pass_score, is_face_required, course_id, lesson_id, created_at, updated_at)
VALUES 
    (1, 'Kiểm tra: Giới thiệu bản thân', 10, 70, false, 1, 4, NOW(), NOW()),
    (2, 'IELTS Speaking Part 1 Practice', 15, 60, false, 3, 19, NOW(), NOW());

-- =====================================================
-- 8. TEST QUESTIONS
-- =====================================================

INSERT INTO test_questions (id, test_id, content, type, correct_schema, created_at, updated_at)
VALUES 
    (1, 1, 'Chọn câu trả lời đúng: "My name ___ John."', 'MCQ', '{"correct_option_id": 2}', NOW(), NOW()),
    (2, 1, 'Điền từ đúng: "I am ___ Vietnam."', 'FILL_BLANK', '{"accepted": ["from", "From"]}', NOW(), NOW()),
    (3, 1, 'Câu nào là cách giới thiệu đúng?', 'MCQ', '{"correct_option_id": 1}', NOW(), NOW()),
    (4, 2, 'What do you do for a living?', 'SPEAKING', '{"sample_answer": "I am a student."}', NOW(), NOW()),
    (5, 2, 'Do you enjoy living in your city?', 'SPEAKING', '{"sample_answer": "Yes, I do."}', NOW(), NOW());

-- =====================================================
-- 9. TEST OPTIONS
-- =====================================================

INSERT INTO test_options (id, question_id, content, is_correct, created_at, updated_at)
VALUES 
    (1, 1, 'am', false, NOW(), NOW()),
    (2, 1, 'is', true, NOW(), NOW()),
    (3, 1, 'are', false, NOW(), NOW()),
    (4, 3, 'My name is Alex. I am from Hanoi.', true, NOW(), NOW()),
    (5, 3, 'Name my Alex.', false, NOW(), NOW()),
    (6, 3, 'I am come from Vietnam.', false, NOW(), NOW());

-- =====================================================
-- 10. CLASSROOMS
-- =====================================================

INSERT INTO classrooms (id, name, teacher_id, start_date, end_date, status, created_at, updated_at)
VALUES 
    (1, 'English Conversation - Beginner (Class A)', 'tea-001', '2026-01-05', '2026-03-28', 'ACTIVE', NOW(), NOW()),
    (2, 'English Conversation - Beginner (Class B)', 'tea-001', '2026-01-10', '2026-04-10', 'ACTIVE', NOW(), NOW()),
    (3, 'Business English (Class A)', 'tea-001', '2026-02-01', '2026-05-01', 'UPCOMING', NOW(), NOW());

-- =====================================================
-- 11. CLASS STUDENTS
-- =====================================================

INSERT INTO class_students (class_id, student_id, joined_at, is_face_verified)
VALUES 
    (1, 'stu-001', NOW(), false),
    (2, 'stu-001', NOW(), false);

-- =====================================================
-- 12. BADGES
-- =====================================================

INSERT INTO badges (id, name, icon_url, condition_rule, created_at, updated_at)
VALUES 
    (1, 'First Lesson', 'https://example.com/badges/first_lesson.png', '{"lessons_completed": 1}', NOW(), NOW()),
    (2, '7 Day Streak', 'https://example.com/badges/7day.png', '{"streak": 7}', NOW(), NOW()),
    (3, 'Vocabulary Master', 'https://example.com/badges/vocab_master.png', '{"words_learned": 100}', NOW(), NOW()),
    (4, 'Speaking Pro', 'https://example.com/badges/speaking.png', '{"speaking_lessons": 10}', NOW(), NOW()),
    (5, 'Perfect Score', 'https://example.com/badges/perfect.png', '{"test_score": 100}', NOW(), NOW());

-- =====================================================
-- 13. USER BADGES
-- =====================================================

INSERT INTO user_badges (id, user_id, badge_id, earned_at, created_at, updated_at)
VALUES 
    (1, 'stu-001', 1, NOW(), NOW(), NOW()),
    (2, 'stu-001', 2, NOW(), NOW(), NOW());

-- =====================================================
-- 14. USER VOCABULARY PROGRESS
-- =====================================================

INSERT INTO user_vocabulary_progress (id, user_id, vocabulary_id, status, times_reviewed, correct_count, created_at, updated_at)
VALUES 
    (1, 'stu-001', 1, 'LEARNING', 2, 1, NOW(), NOW()),
    (2, 'stu-001', 2, 'LEARNING', 2, 2, NOW(), NOW()),
    (3, 'stu-001', 3, 'MASTERED', 5, 5, NOW(), NOW()),
    (4, 'stu-001', 6, 'NEW', 0, 0, NOW(), NOW()),
    (5, 'stu-001', 7, 'NEW', 0, 0, NOW(), NOW());

-- =====================================================
-- 15. XP LOGS
-- =====================================================

INSERT INTO xp_logs (id, user_id, amount, reason, created_at)
VALUES 
    (1, 'stu-001', 50, 'Complete lesson', NOW()),
    (2, 'stu-001', 30, 'Complete speaking', NOW()),
    (3, 'stu-001', 20, 'Learn vocabulary', NOW()),
    (4, 'stu-001', 100, 'Complete test', NOW()),
    (5, 'stu-001', 10, 'Daily bonus', NOW());

-- =====================================================
-- 16. NOTIFICATIONS
-- =====================================================

INSERT INTO notifications (id, user_id, message, is_read, metadata, created_at, updated_at)
VALUES 
    (1, 'stu-001', 'Chào mừng bạn đến với English AI!', false, '{"type": "welcome"}', NOW(), NOW()),
    (2, 'stu-001', 'Bạn đã có streak 7 ngày!', false, '{"type": "streak"}', NOW(), NOW()),
    (3, 'stu-001', 'Bài học mới: Business English', false, '{"type": "new_content"}', NOW(), NOW());

-- =====================================================
-- VERIFY DATA
-- =====================================================

SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Courses', COUNT(*) FROM courses
UNION ALL
SELECT 'Sessions', COUNT(*) FROM sessions
UNION ALL
SELECT 'Lessons', COUNT(*) FROM lessons
UNION ALL
SELECT 'Vocabulary', COUNT(*) FROM vocabularies
UNION ALL
SELECT 'Tests', COUNT(*) FROM tests
UNION ALL
SELECT 'Classrooms', COUNT(*) FROM classrooms
UNION ALL
SELECT 'Badges', COUNT(*) FROM badges;

-- =====================================================
-- ENGLISH LEARNING APP - DATABASE INITIALIZATION
-- =====================================================
-- This script creates all necessary tables and sample data
-- Matching JPA Entity definitions

-- Required for generating bcrypt hashes inside SQL (crypt/gen_salt)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =====================================================
-- 1. ROLES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert roles
INSERT INTO roles (name, description) VALUES 
    ('ROLE_USER', 'Student role - default for learners'),
    ('ROLE_TEACHER', 'Teacher role - can create content'),
    ('ROLE_ADMIN', 'Admin role - full system access')
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 2. PERMISSIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 3. ROLE_PERMISSIONS (Many-to-Many)
-- =====================================================
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- =====================================================
-- 4. USERS TABLE
-- =====================================================
-- Note: id is UUID (String in Java), not BigInt
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,  -- UUID as String
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    role_id BIGINT REFERENCES roles(id),
    level INTEGER DEFAULT 1,
    xp INTEGER DEFAULT 0,
    streak INTEGER DEFAULT 0,
    total_lessons_completed INTEGER DEFAULT 0,
    face_id_enrolled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for email lookup
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- =====================================================
-- 5. USER PROFILES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    dob DATE,
    phone VARCHAR(20),
    country VARCHAR(100),
    learning_goal TEXT,
    level VARCHAR(50),  -- Enum: Beginner, Elementary, Intermediate, etc.
    current_streak INTEGER DEFAULT 0,
    last_activity_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 6. COURSES (CURRICULUM) TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    price INTEGER DEFAULT 0,
    teacher_id VARCHAR(36) REFERENCES users(id),  -- UUID String
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_courses_teacher ON courses(teacher_id);

-- =====================================================
-- 7. SESSIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS sessions (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR NULL,
    order_index INTEGER,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sessions_course ON sessions(course_id);

-- =====================================================
-- 8. LESSONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS lessons (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    session_id BIGINT REFERENCES sessions(id) ON DELETE CASCADE,
    type VARCHAR(50),  -- VIDEO, QUIZ, VOCAB, SPEAKING
    order_index INTEGER,
    content_payload JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_lessons_course ON lessons(course_id);
CREATE INDEX IF NOT EXISTS idx_lessons_session ON lessons(session_id);

-- =====================================================
-- 9. VOCABULARIES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS vocabularies (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(255) NOT NULL,
    phonetic VARCHAR(100),
    audio_url VARCHAR(500),
    meanings JSONB,  -- [{"pos": "noun", "def": "...", "ex": "..."}]
    tags JSONB,       -- ["IELTS", "Food", "A1"]
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vocabularies_word ON vocabularies(word);

-- =====================================================
-- 10. LESSON VOCABULARIES (Many-to-Many)
-- =====================================================
CREATE TABLE IF NOT EXISTS lesson_vocabularies (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT REFERENCES lessons(id) ON DELETE CASCADE,
    vocabulary_id BIGINT REFERENCES vocabularies(id) ON DELETE CASCADE,
    order_index INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 11. CLASSROOMS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS classrooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    teacher_id VARCHAR(36) REFERENCES users(id),  -- UUID String
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),  -- UPCOMING, ACTIVE, COMPLETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_classrooms_teacher ON classrooms(teacher_id);

-- =====================================================
-- 12. CLASS STUDENTS (Enrollment - Composite Key)
-- =====================================================
-- Note: student_id is String (UUID), class_id is BigInt
CREATE TABLE IF NOT EXISTS class_students (
    class_id BIGINT REFERENCES classrooms(id) ON DELETE CASCADE,
    student_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_face_verified BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (class_id, student_id)
);

-- =====================================================
-- 13. TESTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS tests (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration INTEGER,  -- minutes
    pass_score INTEGER,
    is_face_required BOOLEAN DEFAULT FALSE,
    course_id BIGINT REFERENCES courses(id) ON DELETE SET NULL,
    lesson_id BIGINT REFERENCES lessons(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tests_course ON tests(course_id);
CREATE INDEX IF NOT EXISTS idx_tests_lesson ON tests(lesson_id);

-- =====================================================
-- 14. TEST QUESTIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS test_questions (
    id BIGSERIAL PRIMARY KEY,
    test_id BIGINT REFERENCES tests(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    type VARCHAR(50),  -- MCQ, FILL_BLANK, SPEAKING
    correct_schema JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_test_questions_test ON test_questions(test_id);

-- =====================================================
-- 15. TEST OPTIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS test_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES test_questions(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_test_options_question ON test_options(question_id);

-- =====================================================
-- 16. TEST ATTEMPTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS test_attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    test_id BIGINT REFERENCES tests(id) ON DELETE CASCADE,
    score INTEGER,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 17. TEST ANSWERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS test_answers (
    id BIGSERIAL PRIMARY KEY,
    attempt_id BIGINT REFERENCES test_attempts(id) ON DELETE CASCADE,
    question_id BIGINT REFERENCES test_questions(id) ON DELETE CASCADE,
    answer_text TEXT,
    is_correct BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 18. BADGES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    icon_url VARCHAR(500),
    condition_rule JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 19. USER BADGES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS user_badges (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    badge_id BIGINT REFERENCES badges(id) ON DELETE CASCADE,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, badge_id)
);

-- =====================================================
-- 20. USER VOCABULARY PROGRESS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS user_vocabulary_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    vocabulary_id BIGINT REFERENCES vocabularies(id) ON DELETE CASCADE,
    status VARCHAR(50),  -- NEW, LEARNING, MASTERED
    times_reviewed INTEGER DEFAULT 0,
    correct_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, vocabulary_id)
);

-- =====================================================
-- 21. XP LOGS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS xp_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    amount INTEGER NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_xp_logs_user ON xp_logs(user_id);

-- =====================================================
-- 22. NOTIFICATIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);

-- =====================================================
-- 23. FACE VERIFICATION LOGS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS face_verification_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50),  -- SUCCESS, FAILED, PENDING
    confidence_score FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 24. MEDIA FILES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS media_files (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),  -- VIDEO, AUDIO, IMAGE
    file_size BIGINT,
    duration_seconds INTEGER,
    lesson_id BIGINT REFERENCES lessons(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 25. AI CONVERSATIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE,
    topic VARCHAR(255),
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 26. AI MESSAGES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT REFERENCES ai_conversations(id) ON DELETE CASCADE,
    role VARCHAR(50),  -- USER, AI
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 27. AUDIT LOGS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id VARCHAR(36),
    details JSONB,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 28. LESSON VIDEOS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS lesson_videos (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT UNIQUE REFERENCES lessons(id) ON DELETE CASCADE,
    video_url VARCHAR(500) NOT NULL,
    duration_seconds INTEGER,
    thumbnail_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- ========== SAMPLE DATA ==========
-- =====================================================

-- Users (id is UUID String)
-- Password for all: password123 (generated via pgcrypto bcrypt)
INSERT INTO users (id, email, password_hash, full_name, avatar_url, role_id, level, xp, streak, total_lessons_completed, face_id_enrolled) VALUES 
    ('tea-001', 'teacher@demo.com', crypt('password123', gen_salt('bf', 10)), 'Ms. Sarah Johnson', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Sarah', 2, 5, 5000, 30, 50, true),
    ('stu-001', 'student@demo.com', crypt('password123', gen_salt('bf', 10)), 'Nguyen Van A', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user1', 1, 3, 1250, 7, 12, false),
    ('adm-001', 'admin@demo.com', crypt('password123', gen_salt('bf', 10)), 'Admin User', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin', 3, 10, 10000, 100, 0, true);

-- User Profiles
INSERT INTO user_profiles (user_id, level, current_streak) VALUES 
    ('tea-001', 'Advanced', 30),
    ('stu-001', 'Beginner', 7),
    ('adm-001', 'Proficient', 100);

-- Courses
INSERT INTO courses (title, description, thumbnail_url, price, teacher_id) VALUES 
    ('English Conversation - Beginner', 'Khóa học giao tiếp tiếng Anh cơ bản dành cho người mới bắt đầu.', 'https://images.unsplash.com/photo-1543269865-cbf427effbad?w=800', 0, 'tea-001'),
    ('Business English', 'Tiếng Anh chuyên ngành cho môi trường công sở.', 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800', 0, 'tea-001'),
    ('IELTS Speaking & Listening', 'Luyện thi IELTS Speaking và Listening.', 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800', 0, 'tea-001');

-- Sessions
INSERT INTO sessions (title, order_index, course_id) VALUES 
    ('Giới thiệu bản thân', 1, 1),
    ('Chào hỏi và xã giao', 2, 1),
    ('Mua sắm và giao dịch', 3, 1),
    ('Đặt đồ ăn và nhà hàng', 4, 1),
    ('Hỏi đường và di chuyển', 5, 1),
    ('Email viết tiếng Anh', 1, 2),
    ('Cuộc họp trong tiếng Anh', 2, 2),
    ('Thuyết trình chuyên nghiệp', 3, 2),
    ('IELTS Speaking Part 1', 1, 3),
    ('IELTS Speaking Part 2', 2, 3),
    ('IELTS Speaking Part 3', 3, 3),
    ('IELTS Listening Strategies', 4, 3);

-- Lessons
INSERT INTO lessons (title, course_id, session_id, type, order_index, content_payload) VALUES 
    ('Cách phát âm tên riêng tiếng Anh', 1, 1, 'VIDEO', 1, '{"duration": 300}'),
    ('Giới thiệu bản thân cơ bản', 1, 1, 'SPEAKING', 2, '{}'),
    ('Từ vựng về quốc tịch', 1, 1, 'VOCAB', 3, '{}'),
    ('Bài tập: Giới thiệu bản thân', 1, 1, 'QUIZ', 4, '{}'),
    ('Cách chào hỏi lịch sự', 1, 2, 'VIDEO', 1, '{"duration": 240}'),
    ('Các câu hỏi thông thường', 1, 2, 'SPEAKING', 2, '{}'),
    ('Từ vựng: Chào hỏi', 1, 2, 'VOCAB', 3, '{}'),
    ('Mua sắm tại cửa hàng', 1, 3, 'VIDEO', 1, '{"duration": 360}'),
    ('Hỏi giá và trả giá', 1, 3, 'SPEAKING', 2, '{}'),
    ('Từ vựng: Mua sắm', 1, 3, 'VOCAB', 3, '{}'),
    ('Đặt đồ ăn', 1, 4, 'VIDEO', 1, '{"duration": 280}'),
    ('Gọi món tại nhà hàng', 1, 4, 'SPEAKING', 2, '{}'),
    ('Hỏi đường', 1, 5, 'VIDEO', 1, '{"duration": 300}'),
    ('Hướng dẫn đường đi', 1, 5, 'SPEAKING', 2, '{}'),
    ('Cấu trúc email tiếng Anh', 2, 6, 'VIDEO', 1, '{"duration": 400}'),
    ('Cách viết email formal', 2, 6, 'SPEAKING', 2, '{}'),
    ('Giới thiệu IELTS Speaking', 3, 9, 'VIDEO', 1, '{"duration": 350}'),
    ('Các câu hỏi Part 1', 3, 9, 'SPEAKING', 2, '{}'),
    ('Luyện tập: IELTS Part 1', 3, 9, 'QUIZ', 3, '{}');

-- Vocabularies
INSERT INTO vocabularies (word, phonetic, audio_url, meanings, tags) VALUES 
    ('Vietnamese', '/ˌvjetnəˈmiːz/', NULL, '[{"pos": "noun", "def": "Người Việt Nam"}]', '["beginner"]'),
    ('student', '/ˈstjuːdənt/', NULL, '[{"pos": "noun", "def": "Học sinh"}]', '["beginner"]'),
    ('from', '/frɒm/', NULL, '[{"pos": "preposition", "def": "Từ"}]', '["beginner"]'),
    ('live', '/lɪv/', NULL, '[{"pos": "verb", "def": "Sống"}]', '["beginner"]'),
    ('age', '/eɪdʒ/', NULL, '[{"pos": "noun", "def": "Tuổi"}]', '["beginner"]'),
    ('hello', '/həˈləʊ/', NULL, '[{"pos": "interjection", "def": "Xin chào"}]', '["beginner"]'),
    ('nice to meet you', '/naɪs tuː miːt juː/', NULL, '[{"pos": "phrase", "def": "Rất vui được gặp"}]', '["beginner"]'),
    ('how are you', '/haʊ ɑːr juː/', NULL, '[{"pos": "phrase", "def": "Bạn khỏe không?"}]', '["beginner"]'),
    ('goodbye', '/ɡʊdˈbaɪ/', NULL, '[{"pos": "interjection", "def": "Tạm biệt"}]', '["beginner"]'),
    ('see you later', '/siː juː ˈleɪtər/', NULL, '[{"pos": "phrase", "def": "Gặp lại"}]', '["beginner"]'),
    ('how much', '/haʊ mʌtʃ/', NULL, '[{"pos": "phrase", "def": "Bao nhiêu tiền?"}]', '["beginner"]'),
    ('expensive', '/ɪkˈspensɪv/', NULL, '[{"pos": "adjective", "def": "Đắt tiền"}]', '["beginner"]'),
    ('cheap', '/tʃiːp/', NULL, '[{"pos": "adjective", "def": "Rẻ"}]', '["beginner"]'),
    ('discount', '/ˈdɪskaʊnt/', NULL, '[{"pos": "noun", "def": "Giảm giá"}]', '["beginner"]'),
    ('buy', '/baɪ/', NULL, '[{"pos": "verb", "def": "Mua"}]', '["beginner"]'),
    ('restaurant', '/ˈrestrɒnt/', NULL, '[{"pos": "noun", "def": "Nhà hàng"}]', '["beginner"]'),
    ('menu', '/ˈmenjuː/', NULL, '[{"pos": "noun", "def": "Thực đơn"}]', '["beginner"]'),
    ('bill', '/bɪl/', NULL, '[{"pos": "noun", "def": "Hóa đơn"}]', '["beginner"]'),
    ('delicious', '/dɪˈlɪʃəs/', NULL, '[{"pos": "adjective", "def": "Ngon"}]', '["beginner"]'),
    ('hungry', '/ˈhʌŋɡri/', NULL, '[{"pos": "adjective", "def": "Đói"}]', '["beginner"]');

-- Classrooms
INSERT INTO classrooms (name, teacher_id, start_date, end_date, status) VALUES 
    ('English Conversation - Beginner (Class A)', 'tea-001', '2026-01-05', '2026-03-28', 'ACTIVE'),
    ('English Conversation - Beginner (Class B)', 'tea-001', '2026-01-10', '2026-04-10', 'ACTIVE'),
    ('Business English (Class A)', 'tea-001', '2026-02-01', '2026-05-01', 'UPCOMING');

-- Class Students (enrollment)
INSERT INTO class_students (class_id, student_id, is_face_verified) VALUES 
    (1, 'stu-001', false),
    (2, 'stu-001', false);

-- Tests
INSERT INTO tests (title, duration, pass_score, is_face_required, course_id, lesson_id) VALUES 
    ('Kiểm tra: Giới thiệu bản thân', 10, 70, false, 1, 4),
    ('IELTS Speaking Part 1 Practice', 15, 60, false, 3, 19);

-- Test Questions
INSERT INTO test_questions (test_id, content, type, correct_schema) VALUES 
    (1, 'Chọn câu trả lời đúng: "My name ___ John."', 'MCQ', '{"correct_option_id": 2}'),
    (1, 'Điền từ đúng: "I am ___ Vietnam."', 'FILL_BLANK', '{"accepted": ["from", "From"]}'),
    (1, 'Câu nào là cách giới thiệu đúng?', 'MCQ', '{"correct_option_id": 1}'),
    (2, 'What do you do for a living?', 'SPEAKING', '{"sample_answer": "I am a student."}'),
    (2, 'Do you enjoy living in your city?', 'SPEAKING', '{"sample_answer": "Yes, I do."}');

-- Test Options
INSERT INTO test_options (question_id, content, is_correct) VALUES 
    (1, 'am', false),
    (1, 'is', true),
    (1, 'are', false),
    (3, 'My name is Alex. I am from Hanoi.', true),
    (3, 'Name my Alex.', false),
    (3, 'I am come from Vietnam.', false);

-- Badges
INSERT INTO badges (name, icon_url, condition_rule) VALUES 
    ('First Lesson', 'https://example.com/badges/first_lesson.png', '{"lessons_completed": 1}'),
    ('7 Day Streak', 'https://example.com/badges/7day.png', '{"streak": 7}'),
    ('Vocabulary Master', 'https://example.com/badges/vocab_master.png', '{"words_learned": 100}'),
    ('Speaking Pro', 'https://example.com/badges/speaking.png', '{"speaking_lessons": 10}'),
    ('Perfect Score', 'https://example.com/badges/perfect.png', '{"test_score": 100}');

-- User Badges
INSERT INTO user_badges (user_id, badge_id) VALUES 
    ('stu-001', 1),
    ('stu-001', 2);

-- User Vocabulary Progress
INSERT INTO user_vocabulary_progress (user_id, vocabulary_id, status, times_reviewed, correct_count) VALUES 
    ('stu-001', 1, 'LEARNING', 2, 1),
    ('stu-001', 2, 'LEARNING', 2, 2),
    ('stu-001', 3, 'MASTERED', 5, 5),
    ('stu-001', 6, 'NEW', 0, 0),
    ('stu-001', 7, 'NEW', 0, 0);

-- XP Logs
INSERT INTO xp_logs (user_id, amount, reason) VALUES 
    ('stu-001', 50, 'Complete lesson'),
    ('stu-001', 30, 'Complete speaking'),
    ('stu-001', 20, 'Learn vocabulary'),
    ('stu-001', 100, 'Complete test'),
    ('stu-001', 10, 'Daily bonus');

-- Notifications
INSERT INTO notifications (user_id, message, is_read, metadata) VALUES 
    ('stu-001', 'Chào mừng bạn đến với English AI!', false, '{"type": "welcome"}'),
    ('stu-001', 'Bạn đã có streak 7 ngày!', false, '{"type": "streak"}'),
    ('stu-001', 'Bài học mới: Business English', false, '{"type": "new_content"}');

-- =====================================================
-- VERIFICATION QUERY
-- =====================================================
SELECT 'roles' as table_name, COUNT(*) as count FROM roles
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'courses', COUNT(*) FROM courses
UNION ALL
SELECT 'sessions', COUNT(*) FROM sessions
UNION ALL
SELECT 'lessons', COUNT(*) FROM lessons
UNION ALL
SELECT 'vocabularies', COUNT(*) FROM vocabularies
UNION ALL
SELECT 'tests', COUNT(*) FROM tests
UNION ALL
SELECT 'classrooms', COUNT(*) FROM classrooms
UNION ALL
SELECT 'badges', COUNT(*) FROM badges;

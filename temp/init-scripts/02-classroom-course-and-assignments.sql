-- Add course_id to classrooms (nullable FK to courses)
ALTER TABLE classrooms ADD COLUMN IF NOT EXISTS course_id BIGINT NULL;
ALTER TABLE classrooms ADD CONSTRAINT fk_classrooms_course
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL;

-- Create class_lesson_assignments table
CREATE TABLE IF NOT EXISTS class_lesson_assignments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  class_id BIGINT NOT NULL,
  lesson_id BIGINT NOT NULL,
  title VARCHAR(500),
  description TEXT,
  due_at DATETIME NULL,
  max_score INT NULL,
  is_required TINYINT(1) DEFAULT 0,
  order_index INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_cla_class FOREIGN KEY (class_id) REFERENCES classrooms(id) ON DELETE CASCADE,
  CONSTRAINT fk_cla_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);

-- If using PostgreSQL instead of MySQL, use:
-- ALTER TABLE classrooms ADD COLUMN IF NOT EXISTS course_id BIGINT REFERENCES courses(id) ON DELETE SET NULL;
-- CREATE TABLE IF NOT EXISTS class_lesson_assignments (...);

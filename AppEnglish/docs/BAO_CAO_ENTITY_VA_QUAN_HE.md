# BÁO CÁO ENTITY VÀ CÁC QUAN HỆ – English AI Learning Platform

**Ngày cập nhật:** 01/2026  
**Phiên bản:** 1.1  
**Mô tả:** Tài liệu mô tả toàn bộ Entity (JPA), bảng cơ sở dữ liệu tương ứng và các quan hệ giữa chúng.

---

## 1. TỔNG QUAN

### 1.1. Lớp nền (MappedSuperclass – không phải bảng)

| Lớp | Mô tả | Thuộc tính kế thừa |
|-----|-------|--------------------|
| **BaseEntity** | Lớp cha cho hầu hết Entity. Cung cấp ID, `created_at`, `updated_at`. | `id` (PK), `createdAt`, `updatedAt` |

**Lưu ý:** `LessonVideo` không kế thừa `BaseEntity` (dùng `@MapsId` với `lesson_id` làm PK).

---

### 1.2. Danh sách Entity và bảng tương ứng

| # | Entity | Bảng DB | Ghi chú |
|---|--------|---------|---------|
| 1 | User | `users` | Soft delete (`deleted_at`) |
| 2 | UserProfile | `user_profiles` | 1-1 với User |
| 3 | Role | `roles` | Phân quyền |
| 4 | Permission | `permissions` | N-1 với Role (qua `role_permissions`) |
| 5 | Curriculum | `courses` | Khóa học |
| 6 | Session | `sessions` | Chương trong khóa |
| 7 | Lesson | `lessons` | Bài học; có `content_payload` JSONB |
| 8 | LessonVideo | `lesson_videos` | Chi tiết video; PK = `lesson_id` |
| 9 | LessonVocabulary | `lesson_vocabularies` | Lesson–Vocabulary (PK ghép); ngữ cảnh từ vựng theo bài |
| 10 | Vocabulary | `vocabularies` | Từ vựng; `meanings`, `tags` JSONB |
| 11 | UserVocabularyProgress | `user_vocabulary_progress` | Tiến độ SRS; PK ghép (user_id, vocab_id) |
| 12 | Test | `tests` | Bài thi; `course_id`, `lesson_id` (nullable) định danh ngữ cảnh |
| 13 | TestQuestion | `test_questions` | Câu hỏi; `correct_schema` JSONB |
| 14 | TestOption | `test_options` | Đáp án (MCQ) |
| 15 | TestAttempt | `test_attempts` | Lần làm bài của user |
| 16 | TestAnswer | `test_answers` | Đáp án user chọn |
| 17 | Classroom | `classrooms` | Lớp học |
| 18 | ClassStudent | `class_students` | Học sinh trong lớp; PK ghép (class_id, student_id) |
| 19 | Badge | `badges` | Huy hiệu; `condition_rule` JSONB |
| 20 | UserBadge | `user_badges` | User–Badge; PK ghép (user_id, badge_id) |
| 21 | AIConversation | `ai_conversations` | Phiên hội thoại AI; OneToMany → AIMessage |
| 22 | AIMessage | `ai_messages` | Tin nhắn AI; `conversation_id` → AIConversation; `metadata` JSONB |
| 23 | AuditLog | `audit_logs` | Nhật ký thay đổi; `actor_id` (không FK) |
| 24 | FaceVerificationLog | `face_verification_logs` | Log xác thực khuôn mặt |
| 25 | MediaFile | `media_files` | File media |
| 26 | Notification | `notifications` | Thông báo; `metadata` JSONB |
| 27 | XPLog | `xp_logs` | Lịch sử cộng điểm kinh nghiệm |

---

## 2. CHI TIẾT TỪNG ENTITY

### 2.1. User (`users`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK, Identity | Từ BaseEntity |
| email | String | NOT NULL, UNIQUE | Đăng nhập |
| password_hash | String | nullable | Null khi login Google/FB |
| full_name | String | | |
| avatar_url | String | | |
| role_id | Long | FK NOT NULL | → roles.id |
| provider | AuthProvider | NOT NULL | LOCAL, GOOGLE, FACEBOOK |
| provider_id | String | | ID từ OAuth |
| deleted_at | LocalDateTime | | Soft delete |
| created_at | LocalDateTime | | BaseEntity |
| updated_at | LocalDateTime | | BaseEntity |

**Index:** `idx_user_email`, `idx_user_provider`

**Quan hệ:**
- **ManyToOne** → `Role` (`role_id`)

---

### 2.2. UserProfile (`user_profiles`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| user_id | Long | FK | → users.id |
| dob | LocalDate | | |
| phone | String | | |
| country | String | | |
| learning_goal | Text | | |
| level | Level | | Beginner → Proficient |

**Quan hệ:**
- **OneToOne** → `User` (`user_id`)

---

### 2.3. Role (`roles`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| name | String | NOT NULL, UNIQUE | VD: TEACHER, ADMIN |
| description | Text | | |

**Quan hệ:**
- **ManyToMany** ↔ `Permission` qua bảng `role_permissions` (`role_id`, `permission_id`)

---

### 2.4. Permission (`permissions`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| name | String | NOT NULL, UNIQUE | VD: "Create Course" |
| code | String | NOT NULL, UNIQUE | VD: COURSE_CREATE |
| module | String | | ACADEMY, SYSTEM |
| description | Text | | |

**Quan hệ:** Chỉ tham gia ManyToMany với `Role`

---

### 2.5. Curriculum (`courses`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| title | String | | |
| description | Text | | |
| thumbnail_url | String | | |
| price | Integer | | 0 = Free |
| teacher_id | Long | FK | → users.id |

**Quan hệ:**
- **ManyToOne** → `User` (`teacher_id`) – người tạo khóa

---

### 2.6. Session (`sessions`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| title | String | | |
| order_index | Integer | | Thứ tự chương |
| course_id | Long | FK | → courses.id |

**Quan hệ:**
- **ManyToOne** → `Curriculum` (`course_id`)
- **OneToMany** ← `Lesson` (`session_id`, mappedBy `session`)

---

### 2.7. Lesson (`lessons`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| title | String | | |
| course_id | Long | FK | → courses.id |
| session_id | Long | FK | → sessions.id |
| type | LessonType | | VIDEO, QUIZ, VOCAB, SPEAKING |
| order_index | Integer | | |
| content_payload | JSONB | | Cấu hình theo type |

**Quan hệ:**
- **ManyToOne** → `Curriculum` (`course_id`)
- **ManyToOne** → `Session` (`session_id`)
- **OneToOne** ← `LessonVideo` (`lesson`, mappedBy)
- **OneToMany** ← `LessonVocabulary` (`lesson`, mappedBy `lessonVocabularies`)

---

### 2.8. LessonVideo (`lesson_videos`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| lesson_id | Long | PK, FK | → lessons.id (1-1) |
| video_url | String | | |
| transcript_json | JSONB | | Mảng [{start, end, text}] |

**Quan hệ:**
- **OneToOne** → `Lesson` (`lesson_id`, `@MapsId`)

---

### 2.9. LessonVocabulary (`lesson_vocabularies`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| lesson_id | Long | PK, FK | → lessons.id |
| vocab_id | Long | PK, FK | → vocabularies.id |
| order_index | Integer | | Thứ tự từ trong bài (tùy chọn) |

**PK ghép:** `LessonVocabularyId` (lessonId, vocabId)

**Index:** `idx_lesson_vocab_lesson`, `idx_lesson_vocab_vocab`

**Quan hệ:**
- **ManyToOne** → `Lesson` (`lesson_id`)
- **ManyToOne** → `Vocabulary` (`vocab_id`)

---

### 2.10. Vocabulary (`vocabularies`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| word | String | NOT NULL | |
| phonetic | String | | IPA |
| audio_url | String | | |
| meanings | JSONB | | [{pos, def, ex}] |
| tags | JSONB | | ["IELTS","A1"] |

**Index:** `idx_vocab_word`

**Quan hệ:**
- **OneToMany** ← `LessonVocabulary` (`vocabulary`, mappedBy `lessonVocabularies`)
- N-1 từ `UserVocabularyProgress` (`vocab_id`)

---

### 2.11. UserVocabularyProgress (`user_vocabulary_progress`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| user_id | Long | PK, FK | → users.id |
| vocab_id | Long | PK, FK | → vocabularies.id |
| status | LearningStatus | | NEW, LEARNING, REVIEW, MASTERED |
| easiness_factor | Double | | SM-2 |
| interval_days | Integer | | SM-2 |
| next_review_at | LocalDateTime | | SM-2 |

**PK ghép:** `UserVocabId` (userId, vocabId)

**Quan hệ:**
- **ManyToOne** → `User` (`user_id`)
- **ManyToOne** → `Vocabulary` (`vocab_id`)

---

### 2.12. Test (`tests`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| title | String | | |
| duration | Integer | | Phút; null = không giới hạn |
| pass_score | Integer | | Điểm đạt |
| is_face_required | Boolean | | Yêu cầu Face ID |
| course_id | Long | FK, nullable | → courses.id (thi cuối khóa, v.v.) |
| lesson_id | Long | FK, nullable | → lessons.id (quiz gắn bài học) |

**Index:** `idx_test_course`, `idx_test_lesson`

**Quan hệ:**
- **ManyToOne** → `Curriculum` (`course_id`) — thi cấp khóa; null nếu quiz/độc lập
- **ManyToOne** → `Lesson` (`lesson_id`) — quiz theo bài; null nếu thi cấp khóa/độc lập
- **OneToMany** ← `TestQuestion`, `TestAttempt`

---

### 2.13. TestQuestion (`test_questions`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| test_id | Long | FK | → tests.id |
| content | Text | | Nội dung câu hỏi |
| type | QuestionType | | MCQ, FILL_BLANK, SPEAKING |
| correct_schema | JSONB | | Đáp án đúng (không trả FE) |

**Quan hệ:**
- **ManyToOne** → `Test` (`test_id`)
- **OneToMany** ← `TestOption` (không `mappedBy` rõ trong entity, JPA suy ra từ `TestOption.question`)
- **OneToMany** ← `TestAnswer` (`question_id`)

---

### 2.14. TestOption (`test_options`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| question_id | Long | FK | → test_questions.id (mặc định) |
| content | String | | A, B, C… |
| is_correct | Boolean | | |

**Quan hệ:**
- **ManyToOne** → `TestQuestion` (`question`)

---

### 2.15. TestAttempt (`test_attempts`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| test_id | Long | FK | → tests.id |
| user_id | Long | FK | → users.id |
| total_score | Integer | | |
| submitted_at | LocalDateTime | | |

**Quan hệ:**
- **ManyToOne** → `Test` (`test`)
- **ManyToOne** → `User` (`user`)
- **OneToMany** ← `TestAnswer` (`attempt_id`)

---

### 2.16. TestAnswer (`test_answers`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| attempt_id | Long | FK | → test_attempts.id |
| question_id | Long | FK | → test_questions.id |
| user_answer | Text | | Text hoặc ID option |
| is_correct | Boolean | | |
| score_earned | Integer | | |

**Quan hệ:**
- **ManyToOne** → `TestAttempt` (`attempt_id`)
- **ManyToOne** → `TestQuestion` (`question_id`)

---

### 2.17. Classroom (`classrooms`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| name | String | | |
| teacher_id | Long | FK | → users.id |
| start_date | LocalDate | | |
| end_date | LocalDate | | |
| status | ClassStatus | | UPCOMING, ACTIVE, FINISHED |

**Quan hệ:**
- **ManyToOne** → `User` (`teacher_id`)
- **OneToMany** ← `ClassStudent` (qua `ClassStudentId.classId`)

---

### 2.18. ClassStudent (`class_students`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| class_id | Long | PK, FK | → classrooms.id |
| student_id | Long | PK, FK | → users.id |
| joined_at | LocalDateTime | | |
| is_face_verified | Boolean | | |

**PK ghép:** `ClassStudentId` (classId, studentId)

**Quan hệ:**
- **ManyToOne** → `Classroom` (`classroom`, `@MapsId("classId")`)
- **ManyToOne** → `User` (`student`, `@MapsId("studentId")`)

---

### 2.19. Badge (`badges`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| name | String | | |
| icon_url | String | | |
| condition_rule | JSONB | | VD: { "streak": 7 } |

**Quan hệ:** Chỉ tham gia ManyToMany với `User` qua `UserBadge`.

---

### 2.20. UserBadge (`user_badges`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| user_id | Long | PK, FK | → users.id |
| badge_id | Long | PK, FK | → badges.id |
| earned_at | LocalDateTime | | |

**PK ghép:** `UserBadgeId` (userId, badgeId)

**Quan hệ:**
- **ManyToOne** → `User` (`user_id`)
- **ManyToOne** → `Badge` (`badge_id`)

---

### 2.21. AIConversation (`ai_conversations`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| user_id | Long | FK | → users.id |
| title | String | | Tóm tắt chủ đề |
| system_context | Text | | System prompt |
| started_at | LocalDateTime | | |
| ended_at | LocalDateTime | | |

**Index:** `idx_ai_conv_user`, `idx_ai_conv_started`

**Quan hệ:**
- **ManyToOne** → `User` (`user_id`)
- **OneToMany** ← `AIMessage` (`conversation`, mappedBy `messages`)

---

### 2.22. AIMessage (`ai_messages`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| conversation_id | Long | FK | → ai_conversations.id |
| user_id | Long | FK | → users.id |
| content | Text | | |
| sender | SenderRole | | USER, AI |
| metadata | JSONB | | VD: tokens, model |

**Index:** `idx_ai_msg_conversation`, `idx_ai_msg_user`

**Quan hệ:**
- **ManyToOne** → `AIConversation` (`conversation_id`)
- **ManyToOne** → `User` (`user_id`)

---

### 2.23. AuditLog (`audit_logs`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| actor_id | Long | | **Không FK** – tránh ràng buộc khi User bị xóa |
| action | String | NOT NULL | CREATE, UPDATE, DELETE, LOGIN_FAILED… |
| table_name | String | | Bảng bị tác động |
| record_id | Long | | ID bản ghi |
| old_value | JSONB | | Giá trị cũ |
| new_value | JSONB | | Giá trị mới |
| ip_address | String | | |

**Index:** `idx_audit_actor`, `idx_audit_table_record`

---

### 2.24. FaceVerificationLog (`face_verification_logs`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| user_id | Long | FK | → users.id |
| confidence | Float | | 0.0–1.0 |
| is_verified | Boolean | | |
| expired_at | LocalDateTime | | Tự hủy dữ liệu |

**Quan hệ:**
- **ManyToOne** → `User` (`user_id`)

---

### 2.25. MediaFile (`media_files`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| url | String | | |
| type | String | | IMAGE, VIDEO |
| uploaded_by_id | Long | FK | → users.id (tên cột mặc định) |

**Quan hệ:**
- **ManyToOne** → `User` (`uploadedBy`)

---

### 2.26. Notification (`notifications`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| user_id | Long | FK | → users.id (mặc định) |
| message | String | | |
| is_read | Boolean | | default false |
| metadata | JSONB | | |

**Quan hệ:**
- **ManyToOne** → `User` (`user`)

---

### 2.27. XPLog (`xp_logs`)

| Thuộc tính | Kiểu | Ràng buộc | Ghi chú |
|------------|------|-----------|---------|
| id | Long | PK | BaseEntity |
| user_id | Long | FK NOT NULL | → users.id |
| amount | Integer | | Số XP |
| action_type | String | | Loại hành động (cột `action`) |

**Quan hệ:**
- **ManyToOne** → `User` (`user_id`)

---

## 3. SƠ ĐỒ QUAN HỆ (TEXT)

```
┌─────────┐       ┌─────────────┐       ┌────────────┐
│  Role   │───┬───│ role_       │───┬───│ Permission │
└────┬────┘   │   │ permissions │   │   └────────────┘
     │ 1      │   └─────────────┘   │
     │        └─────────────────────┘
     │ *
┌────▼────┐       ┌────────────────┐
│  User   │───────│  UserProfile   │  1-1
└────┬────┘  1-1  └────────────────┘
     │
     │ *  (User là trung tâm, nhiều bảng tham chiếu)
     ├──────────────► Curriculum (teacher_id)
     ├──────────────► Classroom (teacher_id)
     ├──────────────► ClassStudent (student_id)
     ├──────────────► TestAttempt (user_id)
     ├──────────────► AIConversation (user_id) ──* AIMessage (conversation_id)
     ├──────────────► AIMessage (user_id)
     ├──────────────► FaceVerificationLog (user_id)
     ├──────────────► MediaFile (uploaded_by_id)
     ├──────────────► Notification (user_id)
     ├──────────────► XPLog (user_id)
     ├──────────────► UserBadge ──► Badge
     └──────────────► UserVocabularyProgress ──► Vocabulary

┌────────────┐   1   ┌──────────┐   *   ┌────────┐
│ Curriculum │───────│ Session  │───────│ Lesson │
│ (courses)  │       │(sessions)│       │(lessons)│
└─────┬──────┘       └──────────┘       └───┬────┘
      │ *                                   │ 1
      └─────────────────────────────────────┘
           (Lesson cũng có course_id)        │ 1
                                             ▼
                                        ┌─────────────┐
                                        │ LessonVideo │
                                        └─────────────┘
Lesson ──* LessonVocabulary (lesson_id, vocab_id) *── Vocabulary

┌──────┐  1   ┌──────────────┐  *  ┌────────────┐      ┌────────────┐
│ Test │──────│ TestQuestion │─────│ TestOption │      │ TestAttempt│
└──┬───┘      └──────┬───────┘     └────────────┘      └─────┬──────┘
   │                 │                                       │
   │ *               │ *                            ┌────────▼────────┐
   └─────────────────┼─────────────────────────────►│   TestAnswer    │
                     └──────────────────────────────┤ (attempt_id,    │
                                                    │  question_id)   │
                                                    └─────────────────┘
Test: course_id (Curriculum), lesson_id (Lesson); cả hai nullable.

┌───────────┐    *   ┌─────────────┐
│ Classroom │────────│ ClassStudent│  PK: (class_id, student_id)
└─────┬─────┘        └──────┬──────┘
      │ teacher_id          │ student_id
      ▼                     ▼
   [User]                [User]

AuditLog: actor_id (Long, KHÔNG FK) – tham chiếu logic tới users.id
```

---

## 4. TỔNG HỢP LOẠI QUAN HỆ

| Loại | Số lượng | Ví dụ |
|------|----------|--------|
| **OneToOne** | 2 | User ↔ UserProfile; Lesson ↔ LessonVideo |
| **OneToMany / ManyToOne** | 18 | Curriculum → Session; Session → Lesson; Test → TestQuestion; User → TestAttempt, AIConversation, FaceVerificationLog… |
| **ManyToMany** (bảng trung gian rõ) | 2 | Role ↔ Permission (`role_permissions`); User ↔ Badge (`user_badges`); Classroom ↔ User (`class_students`) |
| **PK ghép (Composite Key)** | 4 | `UserVocabularyProgress`, `ClassStudent`, `UserBadge`, `LessonVocabulary` |
| **Tham chiếu logic (không FK)** | 1 | `AuditLog.actor_id` → User |

---

## 5. CÁC TRƯỜNG JSONB

| Entity | Cột | Mục đích |
|--------|-----|----------|
| Lesson | content_payload | Cấu hình theo type: quiz_id, duration… |
| LessonVideo | transcript_json | [{start, end, text}] |
| Vocabulary | meanings, tags | Nghĩa, ví dụ; tags phân loại |
| TestQuestion | correct_schema | Đáp án đúng (MCQ: correct_option_id; Fill: accepted[]) |
| Badge | condition_rule | Điều kiện nhận (VD: streak) |
| AIMessage | metadata | tokens, model… |
| AuditLog | old_value, new_value | Snapshot trước/sau thay đổi |
| Notification | metadata | Dữ liệu bổ sung |

---

## 6. ENUM

| Enum | Các giá trị |
|------|-------------|
| User.AuthProvider | LOCAL, GOOGLE, FACEBOOK |
| UserProfile.Level | Beginner, Elementary, Intermediate, Upper_Intermediate, Advanced, Proficient |
| Lesson.LessonType | VIDEO, QUIZ, VOCAB, SPEAKING |
| TestQuestion.QuestionType | MCQ, FILL_BLANK, SPEAKING |
| Classroom.ClassStatus | UPCOMING, ACTIVE, FINISHED |
| UserVocabularyProgress.LearningStatus | NEW, LEARNING, REVIEW, MASTERED |
| AIMessage.SenderRole | USER, AI |

---

## 7. GỢI Ý CẢI TIẾN (TÙY CHỌN)

1. ~~**AIMessage ↔ AIConversation**~~ **Đã triển khai:** `conversation_id` (ManyToOne → `AIConversation`), `AIConversation.messages` (OneToMany).
2. ~~**Test ↔ Lesson/Curriculum**~~ **Đã triển khai:** `course_id`, `lesson_id` (nullable) trong `Test`.
3. ~~**Vocabulary ↔ Lesson**~~ **Đã triển khai:** Bảng trung gian `LessonVocabulary` (lesson_id, vocab_id, order_index).
4. **@Type(JsonType)** deprecated: Một số entity (Vocabulary, TestQuestion, Badge, UserBadge, AuditLog, AIMessage, MediaFile, Notification) vẫn dùng `@Type(JsonType.class)`. Có thể chuyển sang `@JdbcTypeCode(SqlTypes.JSON)` thống nhất với Lesson, LessonVideo.
5. **TestOption:** Chưa khai báo `@JoinColumn(name = "question_id")`; JPA dùng mặc định `question_id` – nên khai báo rõ để dễ đọc và migrate.

---

## 8. LỊCH SỬ CẬP NHẬT

| Phiên bản | Nội dung |
|-----------|----------|
| 1.0 | Bản đầu: 26 entity, sơ đồ quan hệ, JSONB, Enum. |
| 1.1 | Sửa trùng số mục (2.11→2.12…2.27); xóa dòng trùng trong sơ đồ User; cập nhật ngày và phiên bản. Đảm bảo khớp mã: LessonVocabulary, AIMessage.conversation_id, Test.course_id/lesson_id. |

---

*Tài liệu khớp với mã nguồn Entity JPA tại thời điểm báo cáo.*

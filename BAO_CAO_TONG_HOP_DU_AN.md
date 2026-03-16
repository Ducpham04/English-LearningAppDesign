# Báo cáo tổng hợp dự án – English Learning App

**Cập nhật:** 13/03/2026

Tài liệu tổng hợp về dự án: tổng quan, chức năng hiện tại và công nghệ sử dụng.

---

## 1. Tổng quan dự án

### 1.1. Mục tiêu

**English Learning App** là nền tảng học tiếng Anh trực tuyến với các tính năng:

- Học theo lộ trình (Curriculum – Session – Lesson) với nhiều dạng bài: Video, Phát âm, Hội thoại AI, Từ vựng, Quiz.
- Chấm phát âm theo câu (Azure Speech), transcript tự động từ YouTube/upload file (AWS Transcribe, Kafka), IPA (eSpeak).
- Hội thoại với AI (OpenAI API) trong ngữ cảnh bài học.
- Quản lý lớp học (giáo viên), giao bài tập, theo dõi tiến độ học viên.
- Quản trị nội dung (Topic/Session/Lesson), người dùng, báo cáo.

### 1.2. Phạm vi

| Thành phần | Mô tả |
|------------|--------|
| **Frontend** | Ứng dụng web React (SPA), hỗ trợ Student / Teacher / Admin. |
| **Backend** | REST API Spring Boot, xác thực JWT, tích hợp AWS S3, Kafka, Azure Speech, OpenAI. |
| **Database** | PostgreSQL (JPA/Hibernate). |
| **Hạ tầng bên ngoài** | AWS S3 (lưu file), Kafka (xử lý bất đồng bộ transcribe), Azure Speech (phát âm), OpenAI (chatbot). |

### 1.3. Vai trò người dùng

| Vai trò | Mô tả |
|---------|--------|
| **Student** | Học bài (video, phát âm, AI conversation), làm bài tập/quiz, xem tiến độ, lớp học, từ vựng của tôi. |
| **Teacher** | Quản lý lớp, thêm học viên, giao bài tập, thư viện bài học (tạo bài Video), lịch, phân tích, chấm bài. |
| **Admin** | Quản lý user, Topic/Session/Lesson, Curriculum, Test, nội dung video, báo cáo, Face ID, thông báo, cấu hình. |

---

## 2. Chức năng hiện tại

### 2.1. Học viên (Student)

| Chức năng | Mô tả | Route / API |
|-----------|--------|--------------|
| **Dashboard** | Tổng quan tiến độ, XP, streak, bài học gần đây. | `/`, `GET /v1/student/dashboard` |
| **Sessions** | Danh sách session, chi tiết session và bài học. | `/sessions`, `/sessions/:sessionId` |
| **Bài học Video + Karaoke** | Xem video (YouTube/S3), thoại theo từng câu, chấm phát âm, transcript. | `/lessons/video-conversation/:lessonId` |
| **Bài học Phát âm** | Luyện phát âm từng câu, ghi âm, nhận điểm + feedback (Azure). | `/lessons/pronunciation-lesson/:lessonId`, `POST /pronunciation/score` |
| **Bài học AI Conversation** | Hội thoại với AI trong ngữ cảnh bài học. | `/lessons/ai-conversation-lesson/:lessonId`, `POST /v1/ai/chat` |
| **Flashcard** | Học từ vựng dạng thẻ. | `/lessons/flashcard-lesson/:lessonId` |
| **Hoàn thành bài** | Đánh dấu hoàn thành, cộng XP, cập nhật session progress. | `POST /v1/student/lessons/:lessonId/complete` |
| **Bài kiểm tra** | Danh sách test, làm bài, nộp và xem kết quả. | `/tests`, `/tests/:testId`, `POST /v1/tests/:id/attempts` |
| **Từ vựng của tôi** | Thêm/xóa từ, xem danh sách (có thể lọc theo lesson/session). | `/vocabulary`, `/v1/student/my-vocabulary` |
| **Lớp học** | Xem lớp đã tham gia, bài tập, chi tiết lớp. | `/classes/:classId`, `GET /v1/student/classes` |
| **Tiến độ & Thành tựu** | Màn hình tiến độ, achievements. | `/progress`, `/achievements` |
| **Hồ sơ & Cài đặt** | Profile, cài đặt cá nhân. | `/profile`, `/settings` |

### 2.2. Giáo viên (Teacher)

| Chức năng | Mô tả | Route / API |
|-----------|--------|--------------|
| **Dashboard** | Thống kê lớp, thao tác nhanh (lớp, nội dung). | `/teacher` |
| **Quản lý lớp học** | Danh sách lớp, tạo lớp (tên, khóa học, ngày), xem chi tiết, **xoá lớp**. | `/teacher/classes`, `GET/POST/DELETE /v1/teacher/classes` |
| **Chi tiết lớp** | Thông tin lớp, tab Tổng quan / Lộ trình / Bài tập / Học viên. Thêm bài tập (chọn lesson, hạn nộp), thêm/xóa học viên. Sau khi tạo lớp có thể chuyển sang thêm nội dung bài (modal Giao bài tập). | `/teacher/classes/:classId` |
| **Content Library** | Danh sách bài học từ API (lesson.getAll), lọc theo loại/cấp độ, tạo bài học mới (modal). | `/teacher/lessons` |
| **Tạo bài học Video** | Chọn Khóa học → Session, nhập tên/mô tả/thời gian/thứ tự → Tạo bài học → Thêm video: tab YouTube URL (phân tích transcript) hoặc Upload file (transcribe). Giống luồng Admin. | Modal / `/teacher/create-lesson`, `POST /v1/lessons`, `transcribe.youtube`, `transcribe.upload`, `PUT /v1/lessons/:id/video` |
| **Lộ trình (Curriculum)** | Xem khóa học, session, số bài học; tạo Curriculum (chuyển admin). | `/teacher/curriculum` |
| **Lịch (Schedule)** | Lịch tuần theo lớp (dữ liệu từ teacher classes, startDate). | `/teacher/schedule` |
| **Phân tích (Analytics)** | Thống kê lớp, học viên, bài tập (từ API lớp). | `/teacher/analytics` |
| **Trung tâm chấm bài** | Danh sách lớp, bài nộp (hiện dùng API lớp; chưa có API nộp bài đầy đủ). | `/teacher/grading-center` |
| **Chi tiết học viên** | Xem thông tin học viên (từ getUsers hoặc fallback). | `/teacher/student/:studentId` |
| **Quản lý học viên** | Danh sách (nếu có màn hình). | `/teacher/students` |
| **Cài đặt** | Cài đặt giáo viên. | `/teacher/settings` |

### 2.3. Admin

| Chức năng | Mô tả | Route / API |
|-----------|--------|--------------|
| **Dashboard** | Thống kê tổng quan, thao tác nhanh. | `/admin` |
| **Quản lý người dùng** | Danh sách user, lọc theo role/status. | `/admin/users`, `GET /v1/auth/users` |
| **Nội dung & Bài học** | Báo cáo video lesson, pronunciation; xem chi tiết bài (video + transcript). | `/admin/content`, `GET /v1/admin/content/video-lessons`, `GET /v1/admin/content/pronunciation-*` |
| **Quản lý Topic** | Topic → Session → Lesson; CRUD; thêm bài học Video với tab YouTube / Upload, phân tích transcript, script JSON. | `/admin/topics`, curriculum/session/lesson APIs |
| **Quản lý Curriculum / Session** | CRUD khóa học, session. | `/admin/curriculum`, `/admin/sessions` |
| **Quản lý Test** | Tạo/sửa đề, câu hỏi, đáp án. | `/admin/tests` |
| **Quản lý lớp (admin)** | Xem/quản lý lớp toàn hệ thống. | `/admin/classes` |
| **Face ID** | Quản lý Face ID (nếu có). | `/admin/face-id` |
| **Thông báo** | Quản lý thông báo. | `/admin/notifications` |
| **AI Usage** | Theo dõi sử dụng AI. | `/admin/ai-usage` |
| **Cài đặt hệ thống** | Cấu hình admin. | `/admin/settings` |

### 2.4. Luồng chung (dùng chung nhiều vai trò)

- **Xác thực:** Đăng nhập / Đăng ký, JWT, refresh token, axios interceptor 401.
- **Transcribe:** YouTube URL hoặc upload file → Kafka → worker xử lý → transcript JSON (+ dịch, IPA); FE poll `GET /v1/youtube/:lessonId/transcript`.
- **Phát âm:** Ghi âm (blob) → `POST /pronunciation/score` (Azure) → điểm + feedback.
- **AI Chat:** `POST /v1/ai/chat` (OpenAI hoặc URL cấu hình).

---

## 3. Technical

### 3.1. Backend (AppEnglish)

| Hạng mục | Công nghệ |
|----------|-----------|
| **Framework** | Spring Boot 3.5.9, Java 21 |
| **Build** | Maven |
| **Database** | PostgreSQL, JPA/Hibernate |
| **Bảo mật** | Spring Security, JWT (access + refresh) |
| **API** | REST, base path `/api` (ví dụ `/api/v1/...`) |
| **Tài liệu API** | SpringDoc OpenAPI (Swagger) |
| **Công cụ** | Lombok, MapStruct (nếu dùng), Validation |

**Các module chính (package `modules`):**

- **auth**: Login, Register, Refresh, UserDetails, JWT.
- **userDetail / user**: Profile, achievements.
- **curriculum**: CRUD khóa học (Curriculum).
- **session**: CRUD session, gắn course.
- **lesson**: CRUD lesson, type VIDEO/QUIZ/VOCAB/SPEAKING; `LessonVideo`, transcript JSON; `PUT /:id/video`, `POST /:id/ipa/regenerate`.
- **topic**: Topic CRUD (có thể gắn curriculum/session).
- **teacher**: Lớp (classroom), học viên, bài tập (assignment); CRUD lớp, thêm/xóa học viên, tạo/xóa bài tập.
- **student**: Dashboard, hoàn thành bài, progress (lesson/session), lớp học, từ vựng của tôi.
- **test**: Đề thi, câu hỏi, làm bài, nộp bài, chấm điểm; test manage cho giáo viên/admin.
- **vocabulary**: Thêm/xóa từ vựng cá nhân, danh sách (lesson/session).
- **transcribe**: YouTube transcribe (POST, Kafka), upload transcribe (multipart → S3, Kafka), GET transcript; Kafka producers/consumers; S3 StorageService; AWS Transcribe/Translate, eSpeak IPA.
- **pronunciation**: Chấm phát âm (Azure Speech), FFmpeg xử lý audio, Kafka (upload audio), PronunciationAttempt.
- **ai**: Chatbot REST (`/v1/ai/chat`), tích hợp OpenAI.
- **admin**: Thống kê, content (video lessons, pronunciation), course/session/topic participants, báo cáo.

**Exception handling:** `GlobalExceptionHandler` – ApiException, validation, IllegalArgumentException, DataIntegrityViolationException, 404/500.

### 3.2. Frontend (English Learning App Design)

| Hạng mục | Công nghệ |
|----------|-----------|
| **Framework** | React 18 |
| **Ngôn ngữ** | TypeScript |
| **Build / Dev** | Vite 6.x |
| **Styling** | Tailwind CSS 4, tw-animate |
| **UI Components** | shadcn/ui (Radix), Lucide icons, MUI (một số), Motion |
| **State** | Zustand (authStore, appStore, gamificationStore) |
| **Routing** | React Router 7 |
| **HTTP** | Axios (baseURL `/api`, interceptors 401 refresh) |
| **Form / Validation** | react-hook-form (một số màn) |
| **Biểu đồ** | Recharts |
| **Toast** | Sonner |
| **Khác** | date-fns, canvas-confetti, next-themes (dark/light) |

**Cấu trúc thư mục chính:**

- `src/app`: App.tsx, routes, layout (Sidebar, MobileBottomNav), ProtectedRoute.
- `src/features`: student, teacher, admin, auth, lesson components (VideoConversationLesson, PronunciationLesson, AIConversationLesson, FlashcardLesson…).
- `src/services`: apiService (auth, user, curriculum, session, lesson, test, transcribe, pronunciation, ai, teacher, student, admin, vocabulary, notification…).
- `src/hooks`, `src/shared`, `app/components/ui`: hooks, UI dùng chung.

**API service:** Các module gọi REST theo `BaseResponse<T>`, ví dụ: `auth`, `user`, `curriculum`, `session`, `lesson`, `teacherClasses`, `student`, `test`, `transcribe`, `pronunciation`, `ai`, `admin.content`, `vocabulary`, `notification`.

### 3.3. Hạ tầng / Tích hợp

| Thành phần | Mục đích |
|------------|----------|
| **AWS S3** | Lưu file upload (video/audio), file transcript. |
| **Kafka** | Topics cho YouTube transcribe, upload transcribe, pronunciation audio; xử lý bất đồng bộ. |
| **Azure Speech** | Chấm điểm phát âm (Pronunciation Assessment). |
| **OpenAI API** | Chatbot (hoặc endpoint thay thế qua cấu hình). |
| **eSpeak (espeak-ng)** | Sinh IPA cho transcript. |
| **FFmpeg** | Chuyển mã/chuẩn hoá audio trước khi gửi Azure. |

### 3.4. Chạy dự án

- **Backend:** `cd AppEnglish && ./mvnw spring-boot:run` (cần PostgreSQL, có thể Kafka/S3/Azure/OpenAI theo env).
- **Frontend:** `cd "English Learning App Design" && pnpm install && pnpm dev` (proxy `/api` tới backend).
- **Docker:** Backend có thể chạy bằng Docker Compose (xem `AppEnglish/README.md`).

---

## 4. Tài liệu liên quan

| File | Nội dung |
|------|----------|
| [DANH_MUC_BAO_CAO.md](DANH_MUC_BAO_CAO.md) | Mục lục các báo cáo, hướng dẫn tạo/cập nhật report. |
| [AppEnglish/README.md](AppEnglish/README.md) | Giới thiệu Backend, tính năng, tech stack, Quick Start. |
| [English Learning App Design/report/PROJECT_REPORT_2026-03-09.md](English%20Learning%20App%20Design/report/PROJECT_REPORT_2026-03-09.md) | Báo cáo chi tiết scope, upload/transcript flow, rủi ro. |
| [English Learning App Design/API_SPECIFICATION.md](English%20Learning%20App%20Design/API_SPECIFICATION.md) | Đặc tả REST API. |
| [English Learning App Design/IMPLEMENTATION_SUMMARY.md](English%20Learning%20App%20Design/IMPLEMENTATION_SUMMARY.md) | Tóm tắt tính năng Frontend đã triển khai. |
| [AppEnglish/docs/BAO_CAO_ENTITY_VA_QUAN_HE.md](AppEnglish/docs/BAO_CAO_ENTITY_VA_QUAN_HE.md) | Entity JPA, bảng DB, quan hệ. |

---

*Báo cáo này phản ánh trạng thái dự án tại thời điểm 13/03/2026.*

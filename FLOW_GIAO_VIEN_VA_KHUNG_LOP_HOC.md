# Flow Giáo viên & Khung lớp học – Nội dung GV có thể setup trong lớp

**Cập nhật:** 13/03/2026

Tài liệu mô tả luồng sử dụng của **Giáo viên** và **khung lớp học** – những nội dung giáo viên có thể thiết lập trong một lớp.

---

## 1. Flow tổng thể của Giáo viên

### 1.1. Tạo và quản lý lớp

| Bước | Mô tả | Màn hình / Hành động |
|------|--------|----------------------|
| **1. Tạo lớp** | GV vào **Quản lý lớp** → **Tạo lớp**. Nhập tên lớp, chọn khóa học (tùy chọn), ngày bắt đầu/kết thúc, trạng thái (Sắp diễn ra / Đang hoạt động / Kết thúc). Hệ thống **tự sinh Mã lớp** (6 ký tự, VD: `AB3K9M`) dùng cho học viên xin vào lớp. | `/teacher/classes` → Nút "Tạo lớp" |
| **2. Chia sẻ mã lớp** | Trong **Chi tiết lớp**, header hiển thị **Mã lớp: XXXXXX** và nút copy. GV gửi mã này cho học viên (Zalo, email, in ra…). | `/teacher/classes/:classId` → Banner đầu trang |
| **3. Duyệt học viên vào lớp** | Học viên nhập mã lớp ở trang chủ → gửi yêu cầu. GV vào tab **Học viên** của lớp → mục **Yêu cầu chờ duyệt** → **Duyệt** hoặc **Từ chối** từng yêu cầu. Duyệt xong, HV xuất hiện trong danh sách học viên của lớp. | Tab **Học viên** → "Yêu cầu chờ duyệt" |
| **4. Thêm học viên trực tiếp (tùy chọn)** | GV có thể thêm HV mà không cần họ nhập mã: tab **Học viên** → **Thêm học viên** → chọn tài khoản từ danh sách (role Student). | Tab **Học viên** → "Thêm học viên" |

### 1.2. Chuẩn bị nội dung cho lớp

| Nguồn nội dung | Mô tả |
|----------------|--------|
| **Khóa học (Curriculum)** | Khi tạo lớp, GV có thể gắn **một khóa học** (Curriculum). Khóa này do Admin tạo hoặc do chính GV tạo (Thư viện nội dung). Lộ trình tab **Lộ trình** trong lớp sẽ hiển thị Session & Lesson của khóa đó. |
| **Bài học của GV** | GV tạo bài học Video trong **Thư viện nội dung** (Curriculum của tôi → Session → Tạo bài học → YouTube hoặc Upload → transcript, IPA). Các bài này nằm trong **khóa của GV** và xuất hiện trong dropdown khi GV **giao bài tập** trong lớp. |
| **Bài học toàn hệ thống** | Ngoài bài của GV, GV có thể giao thêm bài từ **lesson.getAll()** (bài do Admin hoặc GV khác tạo) nếu backend/frontend hỗ trợ merge danh sách. |

### 1.3. Giao bài tập & bài kiểm tra trong lớp

| Loại | Cách setup | Ghi chú |
|------|------------|--------|
| **Bài tập (Video / Bài học)** | Tab **Bài tập** → **Thêm bài tập (Video/Bài học)** → Chọn **bài học** (từ danh sách bài của GV + bài khóa lớp), nhập tên bài tập (tùy chọn), hạn nộp (tùy chọn) → **Giao bài**. | Mỗi bài tập = một Lesson được gắn vào lớp; HV vào làm bài (video + phát âm), hoàn thành được lưu tiến độ và điểm. |
| **Bài kiểm tra (Test)** | Tab **Bài tập** → **Thêm bài kiểm tra** → chuyển sang màn **Tạo bài kiểm tra** (`/teacher/create-test`). Tạo/sửa đề, câu hỏi, sau đó giao cho lớp (theo luồng hiện tại của app). | Test do Admin/GV quản lý trong module Test; GV chọn đề có sẵn để giao cho lớp. |

### 1.4. Theo dõi tiến độ trong lớp

| Tab / Chức năng | Nội dung |
|------------------|----------|
| **Tổng quan** | Thông tin lớp: khóa học, trạng thái, ngày bắt đầu/kết thúc, số học viên, số bài tập. |
| **Lộ trình** | Session và danh sách Lesson của khóa học gắn với lớp (chỉ xem, không chỉnh trong lớp). |
| **Bài tập** | Danh sách bài tập đã giao (Video/Bài học); có thể **xóa** từng bài tập. |
| **Tổng hợp** | Bảng theo từng **bài tập**: số HV đã làm / tổng HV, và bảng con theo từng HV (trạng thái, điểm, ngày hoàn thành). |
| **Học viên** | Danh sách HV trong lớp, **yêu cầu chờ duyệt** (nhập mã), **Thêm học viên**, **Xóa** HV khỏi lớp. |

---

## 2. Khung lớp học – Cấu trúc dữ liệu trong một lớp

Một **lớp (Classroom)** gồm:

| Thành phần | Mô tả |
|------------|--------|
| **Thông tin cơ bản** | Tên lớp, trạng thái (UPCOMING / ACTIVE / FINISHED), ngày bắt đầu, ngày kết thúc. |
| **Mã lớp (joinCode)** | Chuỗi 6 ký tự, duy nhất; HV nhập mã để gửi yêu cầu tham gia; GV copy để chia sẻ. |
| **Khóa học (Curriculum)** | Một khóa học tùy chọn gắn với lớp → quy định **Lộ trình** (Session + Lesson) mà GV và HV tham chiếu. |
| **Học viên (ClassStudent)** | Danh sách user (Student) đã được thêm vào lớp: do GV **duyệt yêu cầu** (nhập mã) hoặc **thêm trực tiếp**. |
| **Bài tập (ClassLessonAssignment)** | Danh sách **bài tập** GV giao: mỗi bài = một Lesson (video/phát âm, v.v.) + title, hạn nộp (tùy chọn). HV vào lớp sẽ thấy danh sách bài tập và làm từng bài. |
| **Bài kiểm tra (Test)** | Được giao qua luồng riêng (create-test); kết quả nộp bài theo Test/Attempt. |
| **Yêu cầu vào lớp (ClassJoinRequest)** | Các yêu cầu PENDING từ HV nhập mã; GV duyệt → tạo ClassStudent và chuyển request sang APPROVED. |

---

## 3. Nội dung Giáo viên có thể setup trong lớp (tóm tắt)

| Hạng mục | GV có thể làm |
|----------|----------------|
| **Mã lớp** | Xem và copy mã lớp (tự sinh khi tạo lớp); chia sẻ cho HV để họ xin vào lớp. |
| **Học viên** | Duyệt / từ chối yêu cầu vào lớp (theo mã); thêm HV trực tiếp từ danh sách user; xóa HV khỏi lớp. |
| **Khóa học** | Chọn **một** khóa học khi tạo lớp (hoặc để trống). Lộ trình (Session/Lesson) trong tab Lộ trình lấy từ khóa này. |
| **Bài tập (Video/Bài học)** | Thêm bài tập: chọn lesson (từ bài của GV + bài trong khóa lớp), đặt tên, hạn nộp; xóa bài tập đã giao. |
| **Bài kiểm tra** | Chuyển sang màn tạo/chọn bài kiểm tra và giao cho lớp (theo luồng Test hiện tại). |
| **Theo dõi** | Xem tổng quan lớp; xem số HV đã làm / điểm từng bài tập (tab Tổng hợp); xem danh sách HV. |
| **Xóa lớp** | Xóa lớp (kèm toàn bộ học viên, bài tập, yêu cầu vào lớp). |

---

## 4. Sơ đồ flow (tóm tắt)

```
[GV] Tạo lớp (tên, khóa, ngày) → Hệ thống sinh Mã lớp
        ↓
[GV] Chia sẻ Mã lớp cho HV
        ↓
[HV] Nhập mã lớp (Trang chủ) → Gửi yêu cầu (PENDING)
        ↓
[GV] Vào lớp → Tab Học viên → Yêu cầu chờ duyệt → Duyệt / Từ chối
        ↓ (Duyệt)
[HV] Xuất hiện trong lớp; vào /classes/:id thấy bài tập.

[GV] Tab Bài tập → Thêm bài tập (Video/Bài học): chọn Lesson, tên, hạn
        ↓
[HV] Vào lớp → Chọn bài tập → Làm bài (video, ghi âm, chấm điểm) → Hoàn thành
        ↓
[GV] Tab Tổng hợp → Xem số HV đã làm, điểm từng bài / từng HV.
```

---

## 5. Đường dẫn & API liên quan (tham chiếu nhanh)

| Chức năng | Route (FE) | API (BE) |
|-----------|------------|----------|
| Danh sách lớp | `/teacher/classes` | `GET /v1/teacher/classes` |
| Tạo lớp | (modal trong danh sách lớp) | `POST /v1/teacher/classes` |
| Chi tiết lớp | `/teacher/classes/:classId` | `GET /v1/teacher/classes/:id` |
| Mã lớp | Hiển thị trong header chi tiết lớp | Trả về trong `classInfo.joinCode` |
| Yêu cầu vào lớp | Tab Học viên | `GET /v1/teacher/classes/:id/join-requests` |
| Duyệt / Từ chối yêu cầu | Nút Duyệt / Từ chối | `POST .../join-requests/:requestId/approve`, `.../reject` |
| Thêm HV trực tiếp | Modal Thêm học viên | `POST /v1/teacher/classes/:id/students` |
| Giao bài tập (lesson) | Modal Giao bài tập | `POST /v1/teacher/classes/:id/assignments` |
| Xóa bài tập | Nút xóa trên từng bài | `DELETE /v1/teacher/classes/:id/assignments/:assignmentId` |
| Tổng hợp bài tập | Tab Tổng hợp | `GET /v1/teacher/classes/:id/assignment-stats` |
| Bài học có thể giao | Dropdown trong modal | `GET /v1/teacher/lessons`, fallback `GET /v1/lessons` |
| Thư viện / Tạo bài Video | `/teacher/curriculum`, Create Lesson | `GET /v1/curriculum/teacher/:id`, `POST /v1/lessons`, ... |
| Thêm bài kiểm tra | `/teacher/create-test` | APIs Test management |

---

Tài liệu này mô tả đầy đủ **flow giáo viên** và **khung lớp học** – toàn bộ nội dung GV có thể thiết lập trong một lớp (mã lớp, học viên, bài tập, bài kiểm tra, theo dõi tiến độ).

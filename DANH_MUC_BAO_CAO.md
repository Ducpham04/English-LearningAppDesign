# Danh mục báo cáo dự án – English Learning App

**Cập nhật:** 09/03/2026

Tài liệu này liệt kê toàn bộ file báo cáo của dự án và cách tạo/cập nhật report mới.

---

## 1. Báo cáo hiện có

### 1.1. Báo cáo tổng quan dự án (Frontend + Backend)

| File | Mô tả |
|------|--------|
| [English Learning App Design/report/PROJECT_REPORT_2026-03-09.md](English%20Learning%20App%20Design/report/PROJECT_REPORT_2026-03-09.md) | Báo cáo scope, module Backend/Frontend, API, luồng upload/transcript, rủi ro và bước tiếp theo. |

### 1.2. Báo cáo kỹ thuật Backend (AppEnglish)

| File | Mô tả |
|------|--------|
| [AppEnglish/README.md](AppEnglish/README.md) | Giới thiệu nền tảng, tính năng, tech stack, hướng dẫn chạy (Docker, Maven). |
| [AppEnglish/docs/BAO_CAO_ENTITY_VA_QUAN_HE.md](AppEnglish/docs/BAO_CAO_ENTITY_VA_QUAN_HE.md) | Báo cáo Entity JPA, bảng DB, quan hệ, sơ đồ, JSONB, Enum. |

### 1.3. Tài liệu Frontend

| File | Mô tả |
|------|--------|
| [English Learning App Design/IMPLEMENTATION_SUMMARY.md](English%20Learning%20App%20Design/IMPLEMENTATION_SUMMARY.md) | Tóm tắt tính năng đã triển khai: Video Lesson, Pronunciation, AI Conversation, component hỗ trợ. |
| [English Learning App Design/API_SPECIFICATION.md](English%20Learning%20App%20Design/API_SPECIFICATION.md) | Đặc tả REST API: Auth, User, Class, Session, Lesson, Test, Vocabulary, AI, Gamification, v.v. |

---

## 2. Cách tạo / cập nhật report mới

### 2.1. Report test (Backend – Maven Surefire)

Sau khi cài Java 21 và chạy test, Maven sẽ sinh report trong thư mục `target`:

```bash
cd AppEnglish
./mvnw clean test
```

- **Report test:** `AppEnglish/target/surefire-reports/`
  - Các file `.txt`, `.xml` theo từng test class.
  - Tóm tắt: passed/failed/skipped.

### 2.2. Báo cáo tài liệu (thủ công)

- **Báo cáo tổng quan:** cập nhật nội dung file  
  `English Learning App Design/report/PROJECT_REPORT_2026-03-09.md`  
  (hoặc tạo file mới với ngày, ví dụ `PROJECT_REPORT_YYYY-MM-DD.md`).
- **Entity/DB:** cập nhật  
  `AppEnglish/docs/BAO_CAO_ENTITY_VA_QUAN_HE.md`  
  khi có thay đổi Entity hoặc quan hệ.
- **API:** cập nhật  
  `English Learning App Design/API_SPECIFICATION.md`  
  khi đổi endpoint hoặc contract.
- **Implementation summary:** cập nhật  
  `English Learning App Design/IMPLEMENTATION_SUMMARY.md`  
  khi thêm/sửa tính năng Frontend.

---

## 3. Cấu trúc thư mục báo cáo

```
English-LearningAppDesign/
├── DANH_MUC_BAO_CAO.md          ← File này (mục lục)
├── AppEnglish/
│   ├── README.md                 ← Báo cáo/giới thiệu Backend
│   ├── docs/
│   │   └── BAO_CAO_ENTITY_VA_QUAN_HE.md
│   └── target/
│       └── surefire-reports/     ← Report test (sinh khi chạy mvn test)
└── English Learning App Design/
    ├── report/
    │   └── PROJECT_REPORT_2026-03-09.md
    ├── IMPLEMENTATION_SUMMARY.md
    └── API_SPECIFICATION.md
```

---

*Dùng file này làm mục lục chung để tra cứu nhanh các báo cáo của dự án.*

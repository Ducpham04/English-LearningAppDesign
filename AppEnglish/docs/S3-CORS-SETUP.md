# Cấu hình CORS cho S3 bucket (video phát trong web)

Khi video load trong trang web (origin `http://localhost:5173` hoặc domain production), trình duyệt gửi request **cross-origin** tới S3. Nếu bucket không trả header `Access-Control-Allow-Origin`, trình duyệt sẽ chặn và báo lỗi CORS.

**Mở link video trực tiếp** thì không qua CORS nên vẫn xem được; **embed trong web** thì bị chặn.

## Cách sửa: Bật CORS trên bucket S3

### Bước 1: Vào AWS Console

1. Đăng nhập [AWS Console](https://console.aws.amazon.com/) → **S3**.
2. Chọn bucket **learning-english-bucket**.

### Bước 2: Cấu hình CORS

1. Tab **Permissions** (Quyền).
2. Kéo xuống **Cross-origin resource sharing (CORS)**.
3. Bấm **Edit**.
4. Dán cấu hình dưới đây (thay `http://localhost:5173` bằng origin frontend của bạn; có thể thêm nhiều origin).

### Cấu hình CORS mẫu

```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["GET", "HEAD"],
    "AllowedOrigins": [
      "http://localhost:5173",
      "http://localhost:3000",
      "http://127.0.0.1:5173",
      "https://your-production-domain.com"
    ],
    "ExposeHeaders": ["Content-Length", "Content-Range", "Accept-Ranges"]
  }
]
```

**Giải thích ngắn:**

- **AllowedMethods**: `GET`, `HEAD` — cần cho phát video và range request (tua, tải từng phần).
- **AllowedOrigins**: origin của trang web (Vite dev thường là `http://localhost:5173`). Thay `https://your-production-domain.com` bằng domain production thật khi deploy.
- **ExposeHeaders**: giúp trình duyệt đọc được header range để seek/tua video.

### Bước 3: Lưu

Bấm **Save changes**. Không cần restart app; chỉ cần reload trang web và thử phát lại video.

---

Sau khi lưu CORS, video từ S3 sẽ phát bình thường trong web thay vì bị lỗi CORS.

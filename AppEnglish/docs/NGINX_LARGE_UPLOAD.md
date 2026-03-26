# Upload video > ~1MB bị 413 (Request Entity Too Large)

## Triệu chứng

- File nhỏ (< ~1 MB) upload OK.
- File lớn hơn → trình duyệt báo **`413 Request Entity Too Large`**.
- Đôi khi kèm **CORS** vì response 413 từ **Nginx/proxy** không có header `Access-Control-Allow-*` — lỗi gốc vẫn là **413**, không phải CORS sai trên Spring.

Spring Boot trong repo đã cấu hình `spring.servlet.multipart.max-*` và Tomcat `server.tomcat.max-http-form-post-size` tới **~510MB**. Nếu vẫn 413, request **chưa tới Tomcat** — bị chặn ở **reverse proxy** (Nginx, Caddy, ALB, Cloudflare, …).

---

## Xác định rõ vấn đề (làm theo thứ tự)

Mục tiêu: trả lời **413 do tầng nào** — CDN/proxy hay Spring — rồi mới sửa đúng chỗ.

### Bước 1 — Ghi nhận “mẫu” lỗi

| Quan sát | Ý nghĩa gợi ý |
|----------|----------------|
| Chỉ fail qua domain công khai (`https://api...`) | Gần như chắc **reverse proxy / CDN** trước EC2, không phải code Java. |
| File ~**1 MB** trở lại fail, dưới thì OK | Rất giống **Nginx `client_max_body_size 1m`** (mặc định). |
| Response có header **`Server: nginx`** | 413 có thể do **Nginx** (hoặc Nginx trả trước). |
| Response có **`cf-ray`** / **`server: cloudflare`** | Có **Cloudflare**; thêm giới hạn/tầng khác (xem mục Cloudflare). |
| Gọi **trực tiếp** `http://IP-EC2:8080/api/...` (bỏ qua Nginx) mà upload OK | Chứng minh **Spring/Tomcat ổn**; lỗi nằm ở **Nginx/ALB/Cloudflare** phía trước. |

### Bước 2 — So sánh “qua proxy” vs “thẳng vào app”

Trên máy có `curl` (thay URL và token):

```bash
# A) Đúng như production (qua domain + HTTPS)
curl -sS -o /dev/null -w "%{http_code}\n" -X POST "https://api.engohub.cloud/api/v1/upload/transcribe" \
  -H "Authorization: Bearer YOUR_JWT" \
  -F "lessonId=1" -F "languageCode=en-US" -F "file=@./file_larger_than_1mb.mp4"

# B) Thẳng vào JVM trên EC2 (SSH tunnel hoặc mở tạm security group)
# ví dụ tunnel: ssh -L 8080:127.0.0.1:8080 user@ec2
curl -sS -o /dev/null -w "%{http_code}\n" -X POST "http://127.0.0.1:8080/api/v1/upload/transcribe" \
  -H "Authorization: Bearer YOUR_JWT" \
  -F "lessonId=1" -F "languageCode=en-US" -F "file=@./file_larger_than_1mb.mp4"
```

- **A = 413, B = 202/200** → lỗi ở **Nginx / CDN / ALB**, **không** phải Spring.
- **A và B đều 413** → mới xem Spring/Tomcat (log ứng dụng, `spring.servlet.multipart`, `server.tomcat.max-http-form-post-size`).

### Bước 3 — Xem header response (biết ai trả 413)

```bash
curl -sS -D - -o /dev/null -X POST "https://api.engohub.cloud/api/v1/upload/transcribe" \
  -H "Authorization: Bearer YOUR_JWT" \
  -F "lessonId=1" -F "languageCode=en-US" -F "file=@./big.mp4"
```

Chú ý: `Server`, `Via`, `cf-ray`, `X-Cache`, body HTML “413 Request Entity Too Large” của nginx.

### Bước 4 — Log server

- **Nginx:** `sudo tail -f /var/log/nginx/error.log` — thường có dòng `client intended to send too large body`.
- **Spring:** nếu request tới được controller sẽ có log request; **không có log** khi upload lớn nhưng có 413 → request **chưa vào** Tomcat.

---

## Trước đây upload video lớn vẫn được — vì sao giờ lại 413?

Thường **không phải** do code Spring “tự nhiên” giảm limit, mà do **hạ tầng** đổi. Kiểm tra đã thay đổi gì gần đây:

| Thay đổi hay gặp | Hệ quả |
|------------------|--------|
| Thêm / đổi **Nginx** (site mới, `include` khác, template mặc định) | Mất hoặc chưa có `client_max_body_size` → về mặc định **1m**. |
| Bật **Cloudflare** (orange cloud) cho `api.*` | Thêm giới hạn / hành vi khác; 413 hoặc lỗi khác tùy gói. |
| Đổi từ **IP:8080** sang **domain + Nginx SSL** | Trước đó request không qua Nginx; giờ qua Nginx chưa cấu hình body lớn. |
| **ALB / API Gateway** mới | Có giới hạn body riêng (ít gặp 1m nhưng có thể). |
| Deploy JAR cũ vs mới | Chỉ đáng nghi nếu **Bước 2** cho thấy **cả** gọi thẳng 8080 cũng 413. |

**Kết luận thực tế:** log của bạn (`413` + trước đó upload lớn OK) khớp nhất với **đường đi request hiện tại có thêm một lớp proxy (Nginx/…) với `client_max_body_size` mặc định 1m**. Fix đúng là **sửa cấu hình lớp đó**, không phải chỉ sửa React/Spring (trừ khi Bước 2 chứng minh lỗi ở Tomcat).

## Nginx (phổ biến trên EC2)

Mặc định Nginx dùng **`client_max_body_size 1m`** → đúng pattern “dưới 1MB được”.

Trong `server { }` hoặc `location` proxy tới Spring:

```nginx
client_max_body_size 500M;
client_body_timeout 600s;

location /api/ {
    proxy_pass http://127.0.0.1:8080;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;

    proxy_connect_timeout 600s;
    proxy_send_timeout 600s;
    proxy_read_timeout 600s;
}
```

Sau đó: `sudo nginx -t && sudo systemctl reload nginx`.

### CORS khi proxy trả 4xx/5xx

Nếu FE (vd. `https://engohub.vercel.app`) gọi API subdomain, Nginx có thể trả 413 **không** kèm CORS → Chrome báo CORS. Cách xử lý:

- Thêm header CORS ở Nginx cho API (kể cả lỗi), hoặc
- Đảm bảo không còn 413 (tăng `client_max_body_size`).

Ví dụ (chỉnh domain cho đúng):

```nginx
add_header Access-Control-Allow-Origin "https://engohub.vercel.app" always;
add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS, PATCH" always;
add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
add_header Access-Control-Allow-Credentials "true" always;
```

Với `OPTIONS`, có thể `return 204` sớm. Hoặc để Spring xử lý CORS sau khi body đã qua Nginx.

## Cloudflare (nếu bật trước `api.engohub.cloud`)

- Gói miễn phí có giới hạn upload (thường **100 MB** cho request) — video rất lớn có thể fail với mã khác hoặc timeout.
- Có thể cần **tăng limit**, **bypass cache** cho path upload, hoặc upload **trực tiếp S3 presigned URL** (khuyến nghị cho file rất lớn).

## Biến môi trường production

Trên server API, set:

`APP_CORS_ORIGINS=https://engohub.vercel.app` (thêm domain khác nếu có, cách nhau bởi dấu phẩy).

## Lỗi `service-worker.js` + `chrome-extension`

Không liên quan API — thường do extension Chrome hoặc PWA cache; có thể bỏ qua hoặc tắt extension khi test.

# Chạy local và deploy EC2 (không sửa code mỗi lần)

## Backend (Spring Boot — `AppEnglish`)

**JDK:** build và chạy với **JDK 21** (đã cấu hình trong `pom.xml` + `maven-enforcer-plugin`). Nếu máy có nhiều JDK:

- Kiểm tra: `mvn -v` (dòng Java version phải là 21.x).
- macOS: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`
- IDE: Project SDK / Gradle JVM chọn **21** cho module `AppEnglish`.

**CORS:** Backend đọc `app.cors.allowed-origins` (mặc định trong `application.properties`, có thể override bằng env `APP_CORS_ORIGINS`). Chạy bằng `mvn`/IDE **không** tự load file `.env` của Docker — nếu trước đó bạn chỉ khai báo CORS trong `.env`, hãy export `APP_CORS_ORIGINS` hoặc dựa vào default (đã gồm `http://localhost:5173` + pattern `http://localhost:*`).

| Môi trường | Cách chạy |
|------------|-----------|
| **Máy dev** | `mvn spring-boot:run` hoặc chạy `Application` từ IDE. Mặc định: DB `localhost:5432`, context-path `/api`, port `8080`. **Không** dùng `SPRING_DATASOURCE_URL=...//db:5432/...` khi app chạy ngoài Docker (hostname `db` chỉ có trong network compose). Xem `AppEnglish/.env.example`. |
| **EC2 / prod** | `export SPRING_PROFILES_ACTIVE=prod` rồi chạy JAR. Xem `application-prod.properties`. |

### Biến môi trường thường dùng

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`, `APP_JWT_EXPIRATION`
- `AWS_*`, `AZURE_SPEECH_*`, `OPENAI_API_KEY`, … (theo tính năng)
- **Kafka**
  - **Không** set `SPRING_KAFKA_BOOTSTRAP_SERVERS` thành chuỗi rỗng — sẽ lỗi *No resolvable bootstrap urls*. Mặc định app dùng `localhost:9092` (đúng với nhiều bộ Docker Kafka); port khác → set biến này (vd. `localhost:29092`).
  - **`kafka:29092` / JDBC host `db`** chỉ đúng **trong** Docker network. Dev trên máy: broker qua **`localhost:9092`**, Postgres qua **`localhost:5432`**. Spring Docker Compose có thể inject hostname nội bộ — class `DockerHostServiceConnectionRewriteListener` tự đổi sang `localhost` khi app **không** chạy trong container (có file `/.dockerenv`).
  - Local & mặc định chung: listener **bật** (`spring.kafka.listener.auto-startup=true`) để transcribe/pronunciation qua Kafka hoạt động khi broker đang chạy.
  - Tạm không có broker: `SPRING_KAFKA_LISTENER_AUTO_STARTUP=false`.
  - EC2 chưa có Kafka: cũng có thể `SPRING_KAFKA_LISTENER_AUTO_STARTUP=false` (profile `prod` vẫn cho phép override).

## Frontend (Vite — `English Learning App Design`)

| Môi trường | API base |
|------------|----------|
| **Dev** | Tạo `.env.local` từ `.env.example`, hoặc để trống `VITE_API_BASE_URL` để dùng proxy `/api` → `http://localhost:8080` (xem `vite.config.ts`). |
| **Build prod** | `VITE_API_BASE_URL=https://<host-public-của-API> npm run build` — app tự thêm `/api` nếu thiếu. |

File mẫu: `.env.example`, `.env.production.example`. Không commit `.env.local`.

## Upload video production (413 / CORS giả)

- Triệu chứng: file &lt; ~1 MB OK, lớn hơn → **`413 Request Entity Too Large`**; đôi khi trình duyệt báo **CORS** vì Nginx trả 413 không có header CORS.
- **Xác định đúng tầng lỗi** (curl qua domain vs thẳng `:8080`, header `Server`/`cf-ray`, log nginx): **`AppEnglish/docs/NGINX_LARGE_UPLOAD.md`** — mục *“Xác định rõ vấn đề”* và *“Trước đây upload lớn vẫn được”*.
- Spring trong repo cho tới **500MB**; nếu chỉ fail qua domain → tăng **`client_max_body_size`** (Nginx) và timeout proxy — cùng file doc trên.
- FE production: set **`APP_CORS_ORIGINS`** trên API (vd. `https://engohub.vercel.app`).

## Kiểm tra nhanh

1. PostgreSQL chạy, DB tồn tại (vd. `learning_english`).
2. Backend: `http://localhost:8080/api/swagger-ui.html` (hoặc path swagger trong project).
3. Frontend dev: `npm run dev`, đăng nhập/gọi API như bình thường.

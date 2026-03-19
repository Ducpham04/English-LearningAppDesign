# Chạy local và deploy EC2 (không sửa code mỗi lần)

## Backend (Spring Boot — `AppEnglish`)

**JDK:** build và chạy với **JDK 21** (đã cấu hình trong `pom.xml` + `maven-enforcer-plugin`). Nếu máy có nhiều JDK:

- Kiểm tra: `mvn -v` (dòng Java version phải là 21.x).
- macOS: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`
- IDE: Project SDK / Gradle JVM chọn **21** cho module `AppEnglish`.

| Môi trường | Cách chạy |
|------------|-----------|
| **Máy dev** | `mvn spring-boot:run` hoặc chạy `Application` từ IDE. Mặc định: DB `localhost:5432`, context-path `/api`, port `8080`. |
| **EC2 / prod** | `export SPRING_PROFILES_ACTIVE=prod` rồi chạy JAR. Xem `application-prod.properties`. |

### Biến môi trường thường dùng

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`, `APP_JWT_EXPIRATION`
- `AWS_*`, `AZURE_SPEECH_*`, `OPENAI_API_KEY`, … (theo tính năng)
- **Kafka**
  - **Không** set `SPRING_KAFKA_BOOTSTRAP_SERVERS` thành chuỗi rỗng — sẽ lỗi *No resolvable bootstrap urls*. Mặc định app dùng `localhost:9092` (đúng với nhiều bộ Docker Kafka); port khác → set biến này (vd. `localhost:29092`).
  - Local & mặc định chung: listener **bật** (`spring.kafka.listener.auto-startup=true`) để transcribe/pronunciation qua Kafka hoạt động khi broker đang chạy.
  - Tạm không có broker: `SPRING_KAFKA_LISTENER_AUTO_STARTUP=false`.
  - EC2 chưa có Kafka: cũng có thể `SPRING_KAFKA_LISTENER_AUTO_STARTUP=false` (profile `prod` vẫn cho phép override).

## Frontend (Vite — `English Learning App Design`)

| Môi trường | API base |
|------------|----------|
| **Dev** | Tạo `.env.local` từ `.env.example`, hoặc để trống `VITE_API_BASE_URL` để dùng proxy `/api` → `http://localhost:8080` (xem `vite.config.ts`). |
| **Build prod** | `VITE_API_BASE_URL=https://<host-public-của-API> npm run build` — app tự thêm `/api` nếu thiếu. |

File mẫu: `.env.example`, `.env.production.example`. Không commit `.env.local`.

## Kiểm tra nhanh

1. PostgreSQL chạy, DB tồn tại (vd. `learning_english`).
2. Backend: `http://localhost:8080/api/swagger-ui.html` (hoặc path swagger trong project).
3. Frontend dev: `npm run dev`, đăng nhập/gọi API như bình thường.

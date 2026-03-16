# Chạy dịch IPA (và ffmpeg) trên macOS

Thư mục `tools/` hiện chứa **bản build cho Windows**:

- **eSpeak NG**: `espeak-ng.exe`, `libespeak-ng.dll` + `espeak-ng-data/` — dùng để tạo phiên âm IPA từ text.
- **ffmpeg**: `ffmpeg.exe` — dùng cho xử lý audio (pronunciation scoring, transcode).

Trên **Mac** các file `.exe` / `.dll` **không chạy được**. Để dùng chức năng dịch IPA và xử lý âm thanh trên macOS, bạn cần cài **espeak-ng** và **ffmpeg** qua Homebrew và để app gọi lệnh trong PATH.

## 1. Cài đặt trên Mac

```bash
# Cài espeak-ng (phát âm + IPA) và ffmpeg (xử lý audio)
brew install espeak-ng ffmpeg
```

Kiểm tra:

```bash
espeak-ng --version
ffmpeg -version
```

## 2. Cấu hình ứng dụng khi chạy trên Mac

Backend mặc định dùng:

- `ipa.espeak.command=tools/eSpeak NG/espeak-ng.exe` → chỉ chạy trên Windows.

Trên Mac có hai cách:

### Cách A: Dùng profile `mac` (khuyến nghị)

Đã có file `application-mac.properties` với:

- `ipa.espeak.command=espeak-ng` (gọi lệnh `espeak-ng` trong PATH).
- ffmpeg vẫn dùng `app.ffmpeg.path=ffmpeg` (mặc định từ PATH).

Chạy backend với profile `mac`:

```bash
cd AppEnglish
./mvnw spring-boot:run -Dspring-boot.run.profiles=mac
```

Hoặc khi chạy JAR:

```bash
java -jar app-english.jar --spring.profiles.active=mac
```

### Cách B: Dùng biến môi trường

Không đổi file, chỉ set trước khi chạy:

```bash
export IPA_ESPEAK_COMMAND=espeak-ng
# Sau đó chạy ứng dụng như bình thường
./mvnw spring-boot:run
```

(Spring Boot map `IPA_ESPEAK_COMMAND` → `ipa.espeak.command`.)

## 3. Kết luận

- **Windows**: Dùng sẵn `tools/eSpeak NG/espeak-ng.exe` và (nếu cần) cấu hình `app.ffmpeg.path` trỏ tới `tools/ffmpeg/ffmpeg.exe`.
- **macOS**: **Không** chạy trực tiếp hai tool trong `tools/`. Cài `espeak-ng` và `ffmpeg` bằng Homebrew, rồi chạy app với profile `mac` hoặc `IPA_ESPEAK_COMMAND=espeak-ng`.

Sau khi cấu hình đúng, các tính năng sau sẽ dùng IPA/ffmpeg bình thường trên Mac:

- Tạo/regenerate IPA cho lesson transcript (API hoặc Kafka consumer).
- Chấm điểm phát âm (pronunciation) khi có dùng ffmpeg để transcode audio.

# Vocabulary API Documentation

## 📚 Tổng Quan

Vocabulary API cung cấp các endpoint để quản lý và tìm kiếm từ vựng trong hệ thống học tiếng Anh.

## 🚀 Các Endpoint

### 1. **Lấy danh sách từ vựng với phân trang**

```http
GET /api/v1/vocabularies
```

**Parameters:**
| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `page` | int | 0 | Số trang (bắt đầu từ 0) |
| `size` | int | 20 | Số bản ghi trên một trang |
| `sort` | string | createdAt,desc | Sắp xếp (field,asc/desc) |

**Query Example:**
```http
GET /api/v1/vocabularies?page=0&size=20&sort=createdAt,desc
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "word": "hello",
      "phonetic": "həˈloʊ",
      "audioUrl": "https://example.com/audio/hello.mp3",
      "meanings": [
        {
          "pos": "noun",
          "def": "a polite expression used when meeting someone",
          "ex": "She said hello to me."
        }
      ],
      "tags": ["A1", "Greetings", "IELTS"],
      "createdAt": "2026-01-27 10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 1,
  "totalPages": 1,
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

---

### 2. **Tìm kiếm từ vựng**

```http
GET /api/v1/vocabularies/search?q={keyword}
```

**Parameters:**
| Param | Type | Required | Description |
|-------|------|----------|-------------|
| `q` | string | ✅ Yes | Từ khóa tìm kiếm |
| `page` | int | No | Số trang (mặc định 0) |
| `size` | int | No | Số bản ghi (mặc định 20) |
| `sort` | string | No | Sắp xếp |

**Query Example:**
```http
GET /api/v1/vocabularies/search?q=hello&page=0&size=10
GET /api/v1/vocabularies/search?q=coffee&sort=word,asc
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "word": "hello",
      "phonetic": "həˈloʊ",
      "audioUrl": "https://example.com/audio/hello.mp3",
      "meanings": [...],
      "tags": ["A1", "Greetings"],
      "createdAt": "2026-01-27 10:00:00"
    }
  ],
  "pageable": {...},
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 3. **Lấy chi tiết một từ vựng**

```http
GET /api/v1/vocabularies/{word}
```

**Parameters:**
| Param | Type | Required | Description |
|-------|------|----------|-------------|
| `word` | string | ✅ Yes | Từ vựng cần tìm |

**Query Example:**
```http
GET /api/v1/vocabularies/hello
GET /api/v1/vocabularies/coffee
```

**Response (200 OK):**
```json
{
  "id": 1,
  "word": "hello",
  "phonetic": "həˈloʊ",
  "audioUrl": "https://example.com/audio/hello.mp3",
  "meanings": [
    {
      "pos": "noun",
      "def": "a polite expression used when meeting someone",
      "ex": "She said hello to me."
    }
  ],
  "tags": ["A1", "Greetings", "IELTS"],
  "createdAt": "2026-01-27 10:00:00"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Vocabulary not found"
}
```

---

## 📊 Sorting Options

Hỗ trợ sắp xếp theo các field:

| Field | Description | Example |
|-------|-------------|---------|
| `word` | Theo từ vựng | `?sort=word,asc` |
| `createdAt` | Theo ngày tạo | `?sort=createdAt,desc` |
| `id` | Theo ID | `?sort=id,asc` |

**Multiple Sorts:**
```http
GET /api/v1/vocabularies?sort=createdAt,desc&sort=word,asc
```

---

## 🔍 Pagination Examples

**Trang đầu tiên:**
```http
GET /api/v1/vocabularies?page=0&size=20
```

**Trang thứ 3 với 10 bản ghi:**
```http
GET /api/v1/vocabularies?page=2&size=10
```

**Trang cuối cùng:**
```http
GET /api/v1/vocabularies?page=999&size=20
```

---

## 🧪 Testing with cURL

```bash
# Lấy tất cả từ vựng
curl -X GET "http://localhost:8080/api/v1/vocabularies?page=0&size=20" \
  -H "accept: application/json"

# Tìm kiếm từ vựng
curl -X GET "http://localhost:8080/api/v1/vocabularies/search?q=hello&page=0&size=10" \
  -H "accept: application/json"

# Chi tiết từ vựng
curl -X GET "http://localhost:8080/api/v1/vocabularies/hello" \
  -H "accept: application/json"
```

---

## 📖 Swagger UI

Truy cập Swagger UI để test các API:

```
http://localhost:8080/api/swagger-ui.html
```

Hoặc:

```
http://localhost:8080/api/swagger-ui/
```

---

## ⚠️ Error Responses

| Status | Description |
|--------|-------------|
| `200 OK` | Thành công |
| `404 Not Found` | Từ vựng không tồn tại |
| `400 Bad Request` | Query params không hợp lệ |
| `500 Internal Server Error` | Lỗi server |

---

## 📝 Data Model

```json
## 📝 Data Model (Hệ thống học tập)

```json
{
  "vocabId": 101,
  "word": "persistent",
  "level": "HARD",
  "phonetic": "/pəˈsɪs.tənt/",
  "audioUrl": "[https://api.app/audio/persistent.mp3](https://api.app/audio/persistent.mp3)",
  "meanings": [
    {
      "pos": "adjective",
      "def": "kiên trì, bền bỉ",
      "ex": "She is a persistent student."
    }
  ],
  "tags": ["Tính cách"],
  "createdAt": "2026-01-29 10:36:00"
}
```

---

## 🚀 Running the Application

```bash
# Build
mvn clean install -DskipTests

# Run
mvn spring-boot:run

# Or
java -jar target/AppEnglish-0.0.1-SNAPSHOT.jar
```

**Access API:** `http://localhost:8080/api`

**Access Swagger UI:** `http://localhost:8080/api/swagger-ui.html`

---

## 💡 Tips

1. **Mặc định phân trang:** 20 bản ghi trên một trang
2. **Case-insensitive search:** Tìm kiếm không phân biệt hoa thường
3. **Sorting:** Hỗ trợ sắp xếp tăng (asc) hoặc giảm (desc)
4. **Tags:** Từ vựng có thể có nhiều thẻ để phân loại
5. **Phonetic:** Phiên âm IPA giúp phát âm chính xác


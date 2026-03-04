# Summary of Error Handling Improvements

## Vấn đề Ban Đầu
Quá trình đăng nhập ở FE không hiển thị rõ các error messages từ Backend mặc dù BE đã trả về message chi tiết trong response.

Cấu trúc response từ BE (BaseResponse):
```json
{
  "success": false,
  "message": "Invalid email or password",
  "errorCode": "INVALID_CREDENTIALS",
  "timestamp": "2026-03-04T10:30:00Z",
  "path": "/v1/auth/login"
}
```

Tuy nhiên FE đang cố tìm `err.response?.data?.message` - nơi `message` không nằm trong `data` mà nằm ở level ngoài.

## Giải pháp

### 1. Tạo Error Handler Utility (`src/utils/errorHandler.ts`)
Một utility mới được tạo để cung cấp các hàm tiêu chuẩn để xử lý error messages:

**Các hàm:**
- `getErrorMessage(error, defaultMessage)` - Trích xuất user-friendly error message
- `getErrorCode(error)` - Trích xuất error code từ response
- `isErrorCode(error, code)` - Kiểm tra nếu error là một loại cụ thể
- `extractErrorInfo(error)` - Trích xuất toàn bộ error information
- `logError(source, error, context)` - Log error với format nhất quán

**Thứ tự ưu tiên để tìm message:**
1. `error.response.data.message` (từ BaseResponse)
2. `error.response.data.data.message` (nested data)
3. `error.response.data.error` (alternate field)
4. `error.message` (generic error)
5. Default message

### 2. Cập nhật Login.tsx
```typescript
import { getErrorMessage, logError } from '@/utils/errorHandler';

const handleLogin = async () => {
  setError('');
  if (!email || !password) {
    setError('Please enter both email and password');
    return;
  }
  
  try {
    console.log('[Login] Attempting login for:', email);
    await login({ email, password });
    console.log('[Login] Login successful, state should update user');
  } catch (err: any) {
    logError('Login', err, { email });
    const errorMsg = getErrorMessage(err, 'Login failed');
    setError(errorMsg);
  }
};
```

**Thay đổi:**
- Import error handler utilities
- Sử dụng `logError()` để log chi tiết error (message, code, status, context)
- Sử dụng `getErrorMessage()` để trích xuất message từ BE
- Hiển thị message rõ ràng cho user

### 3. Cập nhật Register.tsx
- Import error handler utilities
- Sử dụng `logError()` và `getErrorMessage()` tương tự như Login

### 4. Cập nhật Profile.tsx
- Import error handler utilities
- Sử dụng `logError()` trong fetch user profile

### 5. Cập nhật AuthStore.ts
- Import `logError`
- Sử dụng `logError()` trong `login()` và `register()` functions
- Thêm context thông tin (email, role) để giúp debug

## Lợi ích

### 1. **Rõ ràng hơn cho User**
Khi có lỗi, user sẽ thấy message rõ ràng từ Backend:
- "Invalid email or password" thay vì lỗi chung chung
- "Email đã tồn tại, vui lòng dùng email khác!" thay vì "Registration failed"

### 2. **Dễ Debug hơn**
Log trong console giúp developer dễ dàng debug:
```
[Login] Error: {
  message: "Invalid email or password",
  code: "INVALID_CREDENTIALS",
  status: 401,
  context: { email: "test@example.com" },
  fullError: {...}
}
```

### 3. **Nhất quán toàn cục**
Tất cả error handling trong FE đều sử dụng cùng utility, đảm bảo nhất quán.

### 4. **Dễ Bảo trì**
Nếu cần thay đổi logic xử lý error, chỉ cần sửa `errorHandler.ts` là ảnh hưởng đến toàn bộ FE.

### 5. **Hỗ trợ nhiều loại Error**
Có thể dễ dàng xử lý các loại error khác nhau dựa trên error code:
```typescript
if (isErrorCode(err, 'DUPLICATE_EMAIL')) {
  // Handle duplicate email
}
```

## Console Output Examples

### Success Case
```
[Axios Request] POST http://localhost:8080/v1/auth/login
{url: "/v1/auth/login", headers: {...}, baseURL: "http://localhost:8080"}

[Axios Response] 200 - POST /v1/auth/login
{data: {success: true, message: "Login successful", data: {...}}}

[Login] Login successful, state should update user
```

### Error Case
```
[Axios Request] POST http://localhost:8080/v1/auth/login
{url: "/v1/auth/login", headers: {...}, baseURL: "http://localhost:8080"}

[Axios Response Error] {
  status: 401,
  url: "/v1/auth/login",
  message: "Request failed with status code 401",
  data: {
    success: false,
    message: "Invalid email or password",
    errorCode: "INVALID_CREDENTIALS",
    ...
  }
}

[Login] Error: {
  message: "Invalid email or password",
  code: "INVALID_CREDENTIALS",
  status: 401,
  context: { email: "test@example.com" },
  fullError: {...}
}
```

UI sẽ hiển thị: **"Invalid email or password"**

## Files Thay đổi

1. ✅ `src/utils/errorHandler.ts` - **Tạo mới**
2. ✅ `src/features/auth/Login.tsx` - Updated
3. ✅ `src/features/auth/Register.tsx` - Updated
4. ✅ `src/features/auth/Profile.tsx` - Updated
5. ✅ `src/store/authStore.ts` - Updated
6. ✅ `ERROR_HANDLING_GUIDE.md` - Tạo mới (Documentation)

## Build Status
✅ **Build successful** - Không có lỗi, project builds thành công

## Kiểm tra Lại
Để kiểm tra error handling hoạt động:

1. Mở console (F12)
2. Thử đăng nhập với email/password sai
3. Quan sát:
   - Console logs chi tiết error (message, code, status, etc.)
   - UI hiển thị error message rõ ràng

Ví dụ console output:
```
[Axios Request] POST ...
[Axios Response Error] status: 401, ...
[Login] Error: { message: "Invalid email or password", code: "INVALID_CREDENTIALS", ... }
```

Ví dụ UI:
```
❌ Invalid email or password
```


# Error Handling Guide for Frontend

## Tổng quan

FE đã được cập nhật để xử lý error messages từ Backend một cách rõ ràng và nhất quán. Tất cả error messages từ BE đều theo cấu trúc `BaseResponse`:

```json
{
  "success": false,
  "message": "Invalid email or password",
  "errorCode": "INVALID_CREDENTIALS",
  "timestamp": "2026-03-04T10:30:00Z",
  "path": "/v1/auth/login"
}
```

## Error Handler Utility

Một utility mới được tạo tại `src/utils/errorHandler.ts` để giúp dễ dàng trích xuất và log error messages.

### Available Functions

#### 1. `getErrorMessage(error, defaultMessage)`
Trích xuất user-friendly error message từ error object.

```typescript
import { getErrorMessage } from '@/utils/errorHandler';

try {
  await login({ email, password });
} catch (err) {
  const errorMsg = getErrorMessage(err, 'Login failed');
  setError(errorMsg); // Hiển thị "Invalid email or password"
}
```

**Thứ tự ưu tiên:**
1. Kiểm tra `error.response.data.message` (từ BaseResponse)
2. Kiểm tra `error.response.data.data.message` (nested data)
3. Kiểm tra `error.response.data.error` (alternate field)
4. Kiểm tra `error.message` (generic error)
5. Dùng default message

#### 2. `getErrorCode(error)`
Trích xuất error code từ response.

```typescript
import { getErrorCode, isErrorCode } from '@/utils/errorHandler';

try {
  await register(...);
} catch (err) {
  if (isErrorCode(err, 'DUPLICATE_EMAIL')) {
    // Handle duplicate email specifically
  }
}
```

#### 3. `extractErrorInfo(error)`
Trích xuất toàn bộ error information.

```typescript
import { extractErrorInfo } from '@/utils/errorHandler';

try {
  await login(...);
} catch (err) {
  const { message, code, status, fullResponse } = extractErrorInfo(err);
  console.log(`Error ${code}: ${message} (Status: ${status})`);
}
```

#### 4. `logError(source, error, context)`
Log error với format nhất quán.

```typescript
import { logError } from '@/utils/errorHandler';

try {
  await someOperation();
} catch (err) {
  logError('MyComponent.operation', err, { userId: 123 });
  // Output:
  // [MyComponent.operation] Error: {
  //   message: "...",
  //   code: "ERROR_CODE",
  //   status: 400,
  //   context: { userId: 123 },
  //   fullError: {...}
  // }
}
```

## Cập nhật trong Các File

### Login.tsx
- Import `getErrorMessage` và `logError`
- Sử dụng `logError` để log error details
- Sử dụng `getErrorMessage` để trích xuất error message

```typescript
import { getErrorMessage, logError } from '@/utils/errorHandler';

const handleLogin = async () => {
  try {
    await login({ email, password });
  } catch (err: any) {
    logError('Login', err, { email });
    const errorMsg = getErrorMessage(err, 'Login failed');
    setError(errorMsg);
  }
};
```

### Register.tsx
- Import `getErrorMessage` và `logError`
- Sử dụng `logError` để log error details
- Sử dụng `getErrorMessage` để trích xuất error message

```typescript
import { getErrorMessage, logError } from '@/utils/errorHandler';

const handleSubmit = async (e: React.FormEvent) => {
  try {
    await register(...);
  } catch (err: any) {
    logError('Register', err, { email });
    const errorMsg = getErrorMessage(err, 'Registration failed');
    setError(errorMsg);
  }
};
```

### Profile.tsx
- Import `logError`
- Sử dụng `logError` trong try-catch block

```typescript
import { logError } from '@/utils/errorHandler';

const fetchUserProfile = async () => {
  try {
    const response = await apiService.user.getMe();
    setProfile(response.data.data);
  } catch (error) {
    logError('Profile.fetchUserProfile', error);
  } finally {
    setIsLoading(false);
  }
};
```

### AuthStore.ts
- Import `logError`
- Sử dụng `logError` trong login và register functions

```typescript
import { logError } from '@/utils/errorHandler';

login: async (credentials) => {
  try {
    // ... login logic
  } catch (error) {
    set({ isLoading: false });
    logError('AuthStore.login', error, { email: credentials.email });
    throw error;
  }
},

register: async (name, email, password, role) => {
  try {
    // ... register logic
  } catch (error) {
    set({ isLoading: false });
    logError('AuthStore.register', error, { email, role });
    throw error;
  }
},
```

## Console Output Examples

### Login Error
```
[Axios Request] POST http://localhost:8080/v1/auth/login
{url: "/v1/auth/login", headers: {...}}

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

### Register Error
```
[Register] Error: {
  message: "Email đã tồn tại, vui lòng dùng email khác!",
  code: "DUPLICATE_EMAIL",
  status: 409,
  context: { email: "test@example.com" },
  fullError: {...}
}
```

## Best Practices

1. **Luôn log error** - Sử dụng `logError()` để log error details để dễ debug
2. **Trích xuất message** - Sử dụng `getErrorMessage()` để lấy message từ BE
3. **Hiển thị cho user** - Hiển thị message rõ ràng trên UI
4. **Thêm context** - Truyền context thông tin vào `logError()` để giúp debug (email, userId, etc.)
5. **Handle error codes** - Sử dụng `isErrorCode()` để xử lý specific error types

## Error Codes từ Backend

| Error Code | HTTP Status | Mô tả |
|---|---|---|
| INVALID_CREDENTIALS | 401 | Email hoặc password không đúng |
| DUPLICATE_EMAIL | 409 | Email đã tồn tại trong hệ thống |
| INVALID_REFRESH_TOKEN | 401 | Refresh token không hợp lệ |
| USER_NOT_FOUND | 404 | User không tìm thấy |
| LOGOUT_FAILED | 500 | Logout thất bại |
| TOKEN_GENERATION_FAILED | 500 | Tạo token thất bại |

## Testing Error Handling

Khi test, hãy mở Console (F12) để xem:
1. Axios request/response logs
2. Error message trích xuất
3. Full error details

Ví dụ:
```
// Test login with invalid credentials
Email: test@example.com
Password: wrongpassword

// Expected console output:
[Axios Request] POST http://localhost:8080/v1/auth/login
[Axios Response Error] status: 401, message: "Invalid email or password"
[Login] Error: message: "Invalid email or password", code: "INVALID_CREDENTIALS"

// Expected UI:
Error message displayed: "Invalid email or password"
```


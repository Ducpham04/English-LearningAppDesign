# 🔌 API Integration Guide

**Version**: 1.0.0  
**Last Updated**: 23/01/2026

---

## Table of Contents

1. [Overview](#1-overview)
2. [API Structure](#2-api-structure)
3. [Authentication](#3-authentication)
4. [Service Implementation](#4-service-implementation)
5. [API Endpoints Mapping](#5-api-endpoints-mapping)
6. [Integration Checklist](#6-integration-checklist)
7. [Error Handling](#7-error-handling)
8. [Testing API Integration](#8-testing-api-integration)

---

## 1. Overview

### Current Status
- Frontend structure: ✅ Ready
- Service layer: ✅ Ready
- Type definitions: ✅ Ready
- Backend API: ⏳ To be created
- External services: ⏳ To be integrated

### Backend Architecture
The frontend is designed to work with a RESTful API backend following this structure:

```
Backend Structure:
├── Authentication Service
├── User Management Service
├── Class Management Service
├── Lesson Service
├── Assessment Service
├── Progress Tracking Service
├── Analytics Service
├── File Upload Service
├── Notification Service
└── AI Integration Service
```

---

## 2. API Structure

### Base Configuration

```typescript
// src/services/apiClient.ts (TO CREATE)

import axios from 'axios';

const API_BASE_URL = process.env.VITE_API_BASE_URL || 'http://localhost:3000/api/v1';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor for adding auth token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor for handling errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - redirect to login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### Environment Configuration

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:3000/api/v1
VITE_STORAGE_BUCKET=dev-bucket
VITE_FACE_API_KEY=xxx
VITE_SPEECH_API_KEY=xxx
VITE_AI_API_KEY=xxx

# .env.production
VITE_API_BASE_URL=https://api.english-ai.com/v1
VITE_STORAGE_BUCKET=prod-bucket
VITE_FACE_API_KEY=xxx
VITE_SPEECH_API_KEY=xxx
VITE_AI_API_KEY=xxx
```

---

## 3. Authentication

### Login Flow

```typescript
// src/services/authService.ts (TO CREATE)

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  success: boolean;
  data: {
    token: string;
    user: {
      id: string;
      email: string;
      name: string;
      role: 'STUDENT' | 'TEACHER' | 'ADMIN';
    };
  };
}

async function login(credentials: LoginRequest): Promise<LoginResponse> {
  const response = await apiClient.post('/auth/login', credentials);
  
  if (response.data.success) {
    // Store token
    localStorage.setItem('auth_token', response.data.data.token);
    
    // Update auth store
    authStore.setState({
      isAuthenticated: true,
      user: response.data.data.user,
    });
  }
  
  return response.data;
}
```

### Token Management

```typescript
// Store JWT token in localStorage
localStorage.setItem('auth_token', token);

// Remove token on logout
localStorage.removeItem('auth_token');

// Refresh token when expired
async function refreshToken() {
  const response = await apiClient.post('/auth/refresh', {
    token: localStorage.getItem('auth_token'),
  });
  
  localStorage.setItem('auth_token', response.data.data.token);
  return response.data.data.token;
}
```

---

## 4. Service Implementation

### Service Pattern

```typescript
// Example: src/services/userService.ts

import apiClient from './apiClient';
import type { User, ApiResponse } from '@/types';

class UserService {
  // Get current user
  async getCurrentUser(): Promise<ApiResponse<User>> {
    return (await apiClient.get('/users/me')).data;
  }

  // Get user by ID
  async getUserById(id: string): Promise<ApiResponse<User>> {
    return (await apiClient.get(`/users/${id}`)).data;
  }

  // Update user profile
  async updateProfile(id: string, data: Partial<User>): Promise<ApiResponse<User>> {
    return (await apiClient.put(`/users/${id}`, data)).data;
  }

  // Get all users (admin)
  async getAllUsers(filters?: any): Promise<ApiResponse<User[]>> {
    return (await apiClient.get('/users', { params: filters })).data;
  }

  // Delete user (admin)
  async deleteUser(id: string): Promise<ApiResponse<void>> {
    return (await apiClient.delete(`/users/${id}`)).data;
  }
}

export const userService = new UserService();
```

### Using Services in Components

```typescript
// Example usage in a React component

import { useEffect, useState } from 'react';
import { userService } from '@/services';
import type { User } from '@/types';

function UserProfile() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        setLoading(true);
        const response = await userService.getCurrentUser();
        if (response.success) {
          setUser(response.data);
        } else {
          setError(response.data.message);
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unknown error');
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>{user?.name}</h1>
      <p>{user?.email}</p>
    </div>
  );
}

export default UserProfile;
```

---

## 5. API Endpoints Mapping

### Authentication Endpoints

```
POST   /auth/register          - Register new user
POST   /auth/login             - Login user
POST   /auth/logout            - Logout user
POST   /auth/refresh           - Refresh JWT token
POST   /auth/forgot-password   - Initiate password reset
POST   /auth/reset-password    - Reset password
POST   /auth/verify-email      - Verify email
GET    /auth/me                - Get current user info
```

### User Management Endpoints

```
GET    /users                  - Get all users (admin)
GET    /users/:id              - Get user by ID
GET    /users/me               - Get current user
PUT    /users/:id              - Update user
DELETE /users/:id              - Delete user (admin)
POST   /users/:id/avatar       - Upload user avatar
GET    /users/:id/progress     - Get user progress
```

### Class Management Endpoints

```
GET    /classes                - Get all classes
POST   /classes                - Create new class (teacher/admin)
GET    /classes/:id            - Get class details
PUT    /classes/:id            - Update class
DELETE /classes/:id            - Delete class
GET    /classes/:id/students   - Get students in class
POST   /classes/:id/students   - Add student to class
DELETE /classes/:id/students/:studentId - Remove student
GET    /classes/:id/analytics  - Get class analytics
```

### Lesson Endpoints

```
GET    /lessons                - Get all lessons
POST   /lessons                - Create lesson (teacher/admin)
GET    /lessons/:id            - Get lesson details
PUT    /lessons/:id            - Update lesson
DELETE /lessons/:id            - Delete lesson
POST   /lessons/:id/assign     - Assign lesson to class/student
GET    /lessons/:id/progress   - Get lesson progress
POST   /lessons/:id/complete   - Mark lesson as complete
```

### Assessment Endpoints

```
GET    /assessments            - Get all assessments
POST   /assessments            - Create assessment
GET    /assessments/:id        - Get assessment details
POST   /assessments/:id/submit - Submit assessment
GET    /assessments/:id/results - Get assessment results
POST   /assessments/:id/grade  - Grade assessment
```

### Progress Tracking Endpoints

```
GET    /progress               - Get user progress
GET    /progress/:userId       - Get user progress (teacher/admin)
POST   /progress/:userId/record - Record progress
GET    /progress/:userId/analytics - Get progress analytics
GET    /progress/class/:classId - Get class progress
```

### Analytics Endpoints

```
GET    /analytics/dashboard    - Get main dashboard data
GET    /analytics/users        - Get user analytics
GET    /analytics/lessons      - Get lesson analytics
GET    /analytics/performance  - Get performance metrics
GET    /analytics/ai-usage     - Get AI API usage
```

### File Upload Endpoints

```
POST   /upload                 - Upload file
POST   /upload/avatar          - Upload user avatar
POST   /upload/lesson-media    - Upload lesson media
POST   /upload/multiple        - Upload multiple files
DELETE /upload/:fileId         - Delete file
```

---

## 6. Integration Checklist

### Phase 1: Authentication ✅ Ready for Integration

**Frontend Readiness**:
- ✅ Login component created
- ✅ Register component created
- ✅ Auth store (Zustand) ready
- ✅ Protected routes configured

**Integration Tasks**:
- [ ] Create `/src/services/authService.ts`
- [ ] Create `/src/services/apiClient.ts`
- [ ] Connect Login component to API
- [ ] Connect Register component to API
- [ ] Implement token management
- [ ] Add request/response interceptors
- [ ] Test authentication flow

**Acceptance Criteria**:
- User can successfully login with valid credentials
- User can register new account
- JWT token is stored securely
- User is redirected on 401 error
- Logout clears token

---

### Phase 2: User Management ✅ Ready for Integration

**Frontend Readiness**:
- ✅ User profile components created
- ✅ User settings pages created
- ✅ Admin user management UI ready

**Integration Tasks**:
- [ ] Create `/src/services/userService.ts`
- [ ] Connect profile pages to API
- [ ] Implement user update functionality
- [ ] Connect admin user management to API
- [ ] Add avatar upload functionality
- [ ] Implement role management

**Acceptance Criteria**:
- User can view their profile
- User can update their profile
- Admin can view all users
- Admin can update user roles
- Avatar upload works correctly

---

### Phase 3: Class Management ✅ Ready for Integration

**Frontend Readiness**:
- ✅ Teacher class management UI created
- ✅ Admin class management UI created
- ✅ Class list and detail views ready

**Integration Tasks**:
- [ ] Create `/src/services/classService.ts`
- [ ] Connect teacher class creation to API
- [ ] Implement student management in class
- [ ] Connect admin class management to API
- [ ] Implement class analytics

**Acceptance Criteria**:
- Teacher can create and manage classes
- Teacher can add/remove students
- Admin can view all classes
- Class analytics display correctly

---

### Phase 4: Lesson Management ✅ Ready for Integration

**Frontend Readiness**:
- ✅ Lesson components created
- ✅ Lesson player UI ready
- ✅ Lesson assignment UI ready

**Integration Tasks**:
- [ ] Create `/src/services/lessonService.ts`
- [ ] Connect lesson listing to API
- [ ] Implement lesson assignment
- [ ] Connect lesson progress tracking
- [ ] Implement lesson completion

**Acceptance Criteria**:
- Students can view assigned lessons
- Students can complete lessons
- Progress is tracked correctly
- Teachers can assign lessons

---

### Phase 5: Assessment & Grading ✅ Ready for Integration

**Frontend Readiness**:
- ✅ Assessment/test UI components created
- ✅ Results display components ready
- ✅ Grading interface created

**Integration Tasks**:
- [ ] Create `/src/services/assessmentService.ts`
- [ ] Connect assessment submission to API
- [ ] Implement auto-grading
- [ ] Connect teacher grading to API
- [ ] Display assessment results

**Acceptance Criteria**:
- Students can submit assessments
- System calculates scores
- Teachers can grade assessments
- Results display with feedback

---

### Phase 6: Analytics & Progress ✅ Ready for Integration

**Frontend Readiness**:
- ✅ Analytics dashboard components ready
- ✅ Progress visualization components ready
- ✅ Chart components integrated

**Integration Tasks**:
- [ ] Create `/src/services/analyticsService.ts`
- [ ] Connect dashboard to real data
- [ ] Implement progress tracking queries
- [ ] Add real-time updates
- [ ] Implement data export

**Acceptance Criteria**:
- Dashboards show real data
- Progress charts update correctly
- Analytics queries are performant

---

### Phase 7: External Services Integration 🟡 Ready for Planning

**Services to Integrate**:

#### Speech Recognition
- [ ] Choose provider (Google Cloud, Azure, Web Speech API)
- [ ] Set up API credentials
- [ ] Create speech service wrapper
- [ ] Integrate pronunciation checking

#### Face Detection/Verification
- [ ] Choose provider (Face-api.js, Azure, Google)
- [ ] Set up biometric service
- [ ] Integrate face enrollment
- [ ] Implement face verification

#### AI Chatbot
- [ ] Choose AI provider (OpenAI, Anthropic, Google)
- [ ] Create AI service wrapper
- [ ] Integrate chat interface
- [ ] Implement context management

#### Analytics Platform
- [ ] Choose analytics provider
- [ ] Set up event tracking
- [ ] Implement custom events
- [ ] Create analytics dashboard

---

## 7. Error Handling

### API Error Types

```typescript
// src/types/errors.ts

interface ApiError {
  code: string;
  message: string;
  details?: Record<string, any>;
  statusCode: number;
}

// Error codes
export const ERROR_CODES = {
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  UNAUTHORIZED: 'UNAUTHORIZED',
  FORBIDDEN: 'FORBIDDEN',
  NOT_FOUND: 'NOT_FOUND',
  CONFLICT: 'CONFLICT',
  RATE_LIMITED: 'RATE_LIMITED',
  SERVER_ERROR: 'SERVER_ERROR',
  NETWORK_ERROR: 'NETWORK_ERROR',
  TIMEOUT: 'TIMEOUT',
};
```

### Error Handling in Services

```typescript
async function apiCall<T>(
  endpoint: string,
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' = 'GET',
  data?: any
): Promise<{ success: boolean; data?: T; error?: ApiError }> {
  try {
    const response = await apiClient[method.toLowerCase()](endpoint, data);
    return { success: true, data: response.data };
  } catch (error) {
    const apiError: ApiError = {
      code: error.response?.data?.error?.code || 'UNKNOWN',
      message: error.response?.data?.error?.message || error.message,
      details: error.response?.data?.error?.details,
      statusCode: error.response?.status || 500,
    };

    // Log error
    console.error('API Error:', apiError);

    // Handle specific errors
    if (apiError.statusCode === 401) {
      // Handle unauthorized
      authStore.logout();
    }

    if (apiError.statusCode === 429) {
      // Handle rate limiting
      showToast('Too many requests. Please try again later.', 'error');
    }

    return { success: false, error: apiError };
  }
}
```

### User-Facing Error Messages

```typescript
// Show user-friendly error messages
function handleApiError(error: ApiError) {
  const errorMessages: Record<string, string> = {
    VALIDATION_ERROR: 'Please check your input and try again',
    UNAUTHORIZED: 'Please login to continue',
    FORBIDDEN: 'You do not have permission',
    NOT_FOUND: 'Resource not found',
    CONFLICT: 'This resource already exists',
    RATE_LIMITED: 'Too many requests. Please try again later',
    SERVER_ERROR: 'Server error occurred. Please try again later',
    NETWORK_ERROR: 'Network connection error',
    TIMEOUT: 'Request timeout. Please check your connection',
  };

  return errorMessages[error.code] || error.message;
}
```

---

## 8. Testing API Integration

### Mock Data for Development

```typescript
// src/services/mockData.ts - Already exists

// Use for testing before API is ready
import { mockData } from '@/services/mockData';

// In components, use conditional logic
const data = process.env.NODE_ENV === 'development' 
  ? mockData.users 
  : await userService.getAllUsers();
```

### Testing Checklist

```
API Integration Testing
├── Authentication
│   ├── [ ] Login with valid credentials
│   ├── [ ] Login with invalid credentials
│   ├── [ ] Register new user
│   ├── [ ] Token refresh
│   └── [ ] Logout
├── Data Fetching
│   ├── [ ] Fetch user list
│   ├── [ ] Fetch with filters
│   ├── [ ] Fetch with pagination
│   └── [ ] Handle empty results
├── Data Mutation
│   ├── [ ] Create resource
│   ├── [ ] Update resource
│   ├── [ ] Delete resource
│   └── [ ] Bulk operations
├── Error Handling
│   ├── [ ] Handle 400 errors
│   ├── [ ] Handle 401 errors
│   ├── [ ] Handle 404 errors
│   ├── [ ] Handle 500 errors
│   └── [ ] Handle network errors
└── Performance
    ├── [ ] Response time < 2s
    ├── [ ] Proper loading states
    └── [ ] Error recovery
```

### Development Workflow

```
1. Frontend development with mock data
   └─ Use mockData.ts for component testing

2. Backend API development
   └─ Create endpoints matching API_SPECIFICATION.md

3. Integration testing
   └─ Connect frontend to real API
   └─ Test all endpoints
   └─ Test error scenarios

4. Performance optimization
   └─ Optimize slow endpoints
   └─ Implement caching
   └─ Reduce payload size

5. Production deployment
   └─ Use production API URL
   └─ Enable monitoring
   └─ Set up alerts
```

---

## Next Steps

1. **Set up backend server** (Node.js/Express recommended)
2. **Create database schema** (PostgreSQL)
3. **Implement authentication API** (highest priority)
4. **Create service wrappers** (for each API endpoint)
5. **Test integration** (unit and E2E tests)
6. **Integrate external services** (speech, face, AI)
7. **Deploy and monitor**

---

*Generated on 23/01/2026*

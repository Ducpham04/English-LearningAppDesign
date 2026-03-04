import axiosInstance from './axiosConfig';
import type { AuthUser, LoginCredentials, RegisterData } from '@/types/auth';

/**
 * Interface for Backend BaseResponse
 */
export interface BaseResponse<T> {
  success: boolean;
  message: string;
  errorCode?: string;
  data: T;
  timestamp: string;
  path: string;
}

/**
 * User Profile Types (matching BE DTOs)
 */
export interface UserProfileRequest {
  fullName?: string;
  avatarUrl?: string;
  settings?: any;
}

/**
 * Pronunciation Types
 */
export interface PronunciationScoreResponse {
  accuracyScore: number;
  fluencyScore: number;
  completenessScore: number;
  pronunciationScore: number;
  words: Array<{
    word: string;
    accuracyScore: number;
    errorType: string;
  }>;
}

/**
 * Transcribe Types
 */
export interface YoutubeTranscribeRequest {
  url: string;
  lessonId: number;
  languageCode?: string;
}

/**
 * API Service
 * Centralized API calls for all modules
 */
const apiService = {
  // --- AUTH MODULE ---
  auth: {
    login: (credentials: LoginCredentials) => 
      axiosInstance.post<BaseResponse<any>>('/v1/auth/login', credentials),
    
    register: (data: any) => 
      axiosInstance.post<BaseResponse<string>>('/v1/auth/register/local', data),
    
    logout: () => 
      axiosInstance.post<BaseResponse<any>>('/v1/auth/logout'),
    
    refresh: (refreshToken: string) => 
      axiosInstance.post<BaseResponse<any>>('/v1/auth/refresh', { refreshToken }),
  },

  // --- USER MODULE ---
  user: {
    getMe: () => 
      axiosInstance.get<BaseResponse<AuthUser>>('/v1/users/me'),
    
    updateMe: (data: UserProfileRequest) => 
      axiosInstance.patch<BaseResponse<AuthUser>>('/v1/users/me', data),
    
    getAllUsers: () => 
      axiosInstance.get<BaseResponse<any[]>>('/v1/auth/users'),
  },

  // --- PRONUNCIATION MODULE ---
  pronunciation: {
    getScore: (lessonId: number, sentenceIndex: number, audioBlob: Blob) => {
      const formData = new FormData();
      formData.append('file', audioBlob, 'recording.wav');
      formData.append('lessonId', lessonId.toString());
      formData.append('sentenceIndex', sentenceIndex.toString());
      
      return axiosInstance.post<BaseResponse<PronunciationScoreResponse>>(
        '/pronunciation/score', 
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );
    },
  },

  // --- TRANSCRIBE MODULE ---
  transcribe: {
    youtube: (data: YoutubeTranscribeRequest) => 
      axiosInstance.post<BaseResponse<string>>('/youtube/transcribe', data),
    
    getTranscript: (lessonId: number) => 
      axiosInstance.get<BaseResponse<any>>(`/youtube/${lessonId}/transcript`),
    
    upload: (lessonId: number, file: File, languageCode: string = 'en-US') => {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('lessonId', lessonId.toString());
      formData.append('languageCode', languageCode);
      
      return axiosInstance.post<BaseResponse<string>>(
        '/upload/transcribe', 
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );
    },
  },

  // --- CLASSROOM MODULE ---
  classroom: {
    getAll: () => 
      axiosInstance.get<BaseResponse<any[]>>('/v1/classrooms'),
    
    getById: (id: number) => 
      axiosInstance.get<BaseResponse<any>>(`/v1/classrooms/${id}`),
    
    getByTeacher: (teacherId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/classrooms/teacher/${teacherId}`),
    
    create: (data: any) => 
      axiosInstance.post<BaseResponse<any>>('/v1/classrooms', data),
    
    update: (id: number, data: any) => 
      axiosInstance.put<BaseResponse<any>>(`/v1/classrooms/${id}`, data),
    
    delete: (id: number) => 
      axiosInstance.delete<BaseResponse<any>>(`/v1/classrooms/${id}`),
  },

  // --- CURRICULUM (COURSE) MODULE ---
  curriculum: {
    getAll: () => 
      axiosInstance.get<BaseResponse<any[]>>('/v1/curriculums'),
    
    getById: (id: number) => 
      axiosInstance.get<BaseResponse<any>>(`/v1/curriculums/${id}`),
    
    getByTeacher: (teacherId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/curriculums/teacher/${teacherId}`),
    
    create: (data: any) => 
      axiosInstance.post<BaseResponse<any>>('/v1/curriculums', data),
    
    update: (id: number, data: any) => 
      axiosInstance.put<BaseResponse<any>>(`/v1/curriculums/${id}`, data),
    
    delete: (id: number) => 
      axiosInstance.delete<BaseResponse<any>>(`/v1/curriculums/${id}`),
  },

  // --- SESSION MODULE ---
  session: {
    getAll: () => 
      axiosInstance.get<BaseResponse<any[]>>('/v1/sessions'),
    
    getById: (id: number) => 
      axiosInstance.get<BaseResponse<any>>(`/v1/sessions/${id}`),
    
    getByCourse: (courseId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/sessions/course/${courseId}`),
    
    create: (data: any) => 
      axiosInstance.post<BaseResponse<any>>('/v1/sessions', data),
    
    update: (id: number, data: any) => 
      axiosInstance.put<BaseResponse<any>>(`/v1/sessions/${id}`, data),
    
    delete: (id: number) => 
      axiosInstance.delete<BaseResponse<any>>(`/v1/sessions/${id}`),
  },

  // --- LESSON MODULE ---
  lesson: {
    getAll: () => 
      axiosInstance.get<BaseResponse<any[]>>('/v1/lessons'),
    
    getById: (id: number) => 
      axiosInstance.get<BaseResponse<any>>(`/v1/lessons/${id}`),
    
    getByCourse: (courseId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/lessons/course/${courseId}`),
    
    getBySession: (sessionId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/lessons/session/${sessionId}`),
    
    create: (data: any) => 
      axiosInstance.post<BaseResponse<any>>('/v1/lessons', data),
    
    update: (id: number, data: any) => 
      axiosInstance.put<BaseResponse<any>>(`/v1/lessons/${id}`, data),
    
    delete: (id: number) => 
      axiosInstance.delete<BaseResponse<any>>(`/v1/lessons/${id}`),
  },

  // --- TEST MODULE ---
  test: {
    getAll: () => 
      axiosInstance.get<BaseResponse<any[]>>('/v1/tests'),
    
    getById: (id: number) => 
      axiosInstance.get<BaseResponse<any>>(`/v1/tests/${id}`),
    
    getByCourse: (courseId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/tests/course/${courseId}`),
    
    getByLesson: (lessonId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/tests/lesson/${lessonId}`),
    
    create: (data: any) => 
      axiosInstance.post<BaseResponse<any>>('/v1/tests', data),
    
    update: (id: number, data: any) => 
      axiosInstance.put<BaseResponse<any>>(`/v1/tests/${id}`, data),
    
    delete: (id: number) => 
      axiosInstance.delete<BaseResponse<any>>(`/v1/tests/${id}`),
  },

  // --- NOTIFICATION MODULE ---
  notification: {
    getByUser: (userId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/notifications/user/${userId}`),
    
    getUnread: (userId: number) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/notifications/user/${userId}/unread`),
    
    create: (data: any) => 
      axiosInstance.post<BaseResponse<any>>('/v1/notifications', data),
    
    markAsRead: (id: number) => 
      axiosInstance.patch<BaseResponse<any>>(`/v1/notifications/${id}/read`),
    
    delete: (id: number) => 
      axiosInstance.delete<BaseResponse<any>>(`/v1/notifications/${id}`),
  },

  // --- VOCABULARY MODULE ---
  vocabulary: {
    getAll: () => 
      axiosInstance.get<BaseResponse<any[]>>('/v1/vocabularies'),
    
    getById: (id: number) => 
      axiosInstance.get<BaseResponse<any>>(`/v1/vocabularies/${id}`),
    
    search: (word: string) => 
      axiosInstance.get<BaseResponse<any[]>>(`/v1/vocabularies/search?word=${encodeURIComponent(word)}`),
    
    create: (data: any) => 
      axiosInstance.post<BaseResponse<any>>('/v1/vocabularies', data),
    
    update: (id: number, data: any) => 
      axiosInstance.put<BaseResponse<any>>(`/v1/vocabularies/${id}`, data),
    
    delete: (id: number) => 
      axiosInstance.delete<BaseResponse<any>>(`/v1/vocabularies/${id}`),
  },

  // --- STUDENT MODULE ---
  student: {
    getDashboard: () => 
      axiosInstance.get<BaseResponse<any>>('/v1/student/dashboard'),
    
    getCourses: () => 
      axiosInstance.get<BaseResponse<any>>('/v1/student/courses'),

    /** Mark lesson as complete and save progress (XP, totalLessonsCompleted). */
    completeLesson: (lessonId: string | number, score?: number) =>
      axiosInstance.post<BaseResponse<{
        xpEarned: number;
        level: number;
        xp: number;
        totalLessonsCompleted: number;
      }>>(`/v1/student/lessons/${lessonId}/complete`, { score }),
  },
};

export default apiService;

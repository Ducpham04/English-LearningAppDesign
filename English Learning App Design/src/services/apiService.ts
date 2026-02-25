import axiosInstance from './axiosConfig';
import type { AuthUser, LoginCredentials, RegisterData } from '@/types/auth';

/**
 * Interface for Backend BaseResponse
 */
export interface BaseResponse<T> {
  data: T;
  message: string;
  path: string;
  status: string;
  timestamp: string;
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
};

export default apiService;
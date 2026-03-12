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
 * Pronunciation Types (match BE PronunciationScoreResponse)
 */
export interface PronunciationScoreResponse {
  attemptId?: number;
  lessonId?: number;
  sentenceIndex?: number;
  expectedText?: string;
  recognizedText?: string;
  pronunciationScore?: number;
  accuracyScore?: number;
  fluencyScore?: number;
  prosodyScore?: number;
  completenessScore?: number;
  status?: string;
  s3Status?: string;
  feedback?: Record<string, unknown>;
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
    getScore: (lessonId: number, sentenceIndex: number, audioBlob: Blob, expectedText?: string) => {
      const formData = new FormData();
      // Tên file theo đúng type của blob (MediaRecorder thường ra webm/opus) để backend/ffmpeg nhận đúng format.
      const ext = audioBlob.type?.includes('webm') ? '.webm' : audioBlob.type?.includes('ogg') ? '.ogg' : audioBlob.type?.includes('wav') ? '.wav' : '.webm';
      const filename = `recording${ext}`;
      formData.append('file', audioBlob, filename);
      formData.append('lessonId', lessonId.toString());
      formData.append('sentenceIndex', sentenceIndex.toString());
      if (expectedText && expectedText.trim()) {
        formData.append('expectedText', expectedText.trim());
      }
      // Không set Content-Type: để browser tự set multipart/form-data; boundary=... (bắt buộc cho server parse đúng).
      return axiosInstance.post<BaseResponse<PronunciationScoreResponse>>(
        '/pronunciation/score',
        formData
      );
    },
  },

  // --- TRANSCRIBE MODULE ---
  transcribe: {
    // Submit YouTube URL → Kafka async → returns transactionId
    youtube: (data: YoutubeTranscribeRequest) =>
      axiosInstance.post<BaseResponse<string>>('/v1/youtube/transcribe', data),

    // Poll status: GET transcript JSON (null/empty = still processing)
    getTranscript: (lessonId: number) =>
      axiosInstance.get<BaseResponse<any[]>>(`/v1/youtube/${lessonId}/transcript`),

    upload: (lessonId: number, file: File, languageCode: string = 'en-US') => {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('lessonId', lessonId.toString());
      formData.append('languageCode', languageCode);
      return axiosInstance.post<BaseResponse<string>>(
        '/v1/upload/transcribe',
        formData
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

  // --- TEACHER CLASSES (v1/teacher/classes) ---
  teacherClasses: {
    getAll: () =>
      axiosInstance.get<BaseResponse<any[]>>('/v1/teacher/classes'),
    create: (data: { name: string; courseId?: number; startDate?: string; endDate?: string; status?: string }) =>
      axiosInstance.post<BaseResponse<any>>('/v1/teacher/classes', data),
    getById: (classId: number) =>
      axiosInstance.get<BaseResponse<any>>(`/v1/teacher/classes/${classId}`),
    getStudents: (classId: number) =>
      axiosInstance.get<BaseResponse<any[]>>(`/v1/teacher/classes/${classId}/students`),
    addStudent: (classId: number, studentId: string) =>
      axiosInstance.post<BaseResponse<any>>(`/v1/teacher/classes/${classId}/students`, { studentId }),
    removeStudent: (classId: number, studentId: string) =>
      axiosInstance.delete<BaseResponse<void>>(`/v1/teacher/classes/${classId}/students/${studentId}`),
    getAssignments: (classId: number) =>
      axiosInstance.get<BaseResponse<any[]>>(`/v1/teacher/classes/${classId}/assignments`),
    createAssignment: (classId: number, data: { lessonId: number; title?: string; description?: string; dueAt?: string; maxScore?: number; isRequired?: boolean; orderIndex?: number }) =>
      axiosInstance.post<BaseResponse<any>>(`/v1/teacher/classes/${classId}/assignments`, data),
    updateAssignment: (classId: number, assignmentId: number, data: { title?: string; description?: string; dueAt?: string; maxScore?: number; isRequired?: boolean; orderIndex?: number }) =>
      axiosInstance.put<BaseResponse<any>>(`/v1/teacher/classes/${classId}/assignments/${assignmentId}`, data),
    deleteAssignment: (classId: number, assignmentId: number) =>
      axiosInstance.delete<BaseResponse<void>>(`/v1/teacher/classes/${classId}/assignments/${assignmentId}`),
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

    saveVideo: (id: number, data: { videoUrl?: string; transcriptJson?: any[] }) =>
      axiosInstance.put<BaseResponse<any>>(`/v1/lessons/${id}/video`, data),

    regenerateIpa: (id: number) =>
      axiosInstance.post<BaseResponse<any>>(`/v1/lessons/${id}/ipa/regenerate`),
  },

  // --- TEST MODULE ---
  test: {
    getAll: () =>
      axiosInstance.get<BaseResponse<any[]>>('/v1/tests'),

    getForTaking: (id: number) =>
      axiosInstance.get<BaseResponse<any>>(`/v1/tests/${id}/take`),

    submitAttempt: (id: number, data: { answers: { questionId: number; userAnswer: string }[] }) =>
      axiosInstance.post<BaseResponse<any>>(`/v1/tests/${id}/attempts`, data),

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

  // --- TEST MANAGEMENT (Admin/Teacher) ---
  testManage: {
    getAll: () =>
      axiosInstance.get<BaseResponse<any[]>>('/v1/tests/manage'),

    getById: (id: number) =>
      axiosInstance.get<BaseResponse<any>>(`/v1/tests/manage/${id}`),

    create: (data: any) =>
      axiosInstance.post<BaseResponse<any>>('/v1/tests/manage', data),

    update: (id: number, data: any) =>
      axiosInstance.put<BaseResponse<any>>(`/v1/tests/manage/${id}`, data),

    delete: (id: number) =>
      axiosInstance.delete<BaseResponse<any>>(`/v1/tests/manage/${id}`),

    addQuestion: (testId: number, data: any) =>
      axiosInstance.post<BaseResponse<any>>(`/v1/tests/manage/${testId}/questions`, data),

    deleteQuestion: (questionId: number) =>
      axiosInstance.delete<BaseResponse<any>>(`/v1/tests/manage/questions/${questionId}`),
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

  // --- ADMIN MODULE ---
  admin: {
    getStats: () =>
      axiosInstance.get<any>('/v1/admin/stats'),
  },

  // --- AI CHAT (Chatbot) ---
  ai: {
    chat: (params: { messages: { role: string; content: string }[]; lessonContext?: string }) =>
      axiosInstance.post<BaseResponse<{ content: string }>>('/v1/ai/chat', params),
  },

  // --- TOPIC MODULE ---
  topic: {
    getActive: () =>
      axiosInstance.get<BaseResponse<any[]>>('/v1/topics'),

    getAll: () =>
      axiosInstance.get<BaseResponse<any[]>>('/v1/topics/all'),

    getById: (id: number) =>
      axiosInstance.get<BaseResponse<any>>(`/v1/topics/${id}`),

    create: (data: any) =>
      axiosInstance.post<BaseResponse<any>>('/v1/topics', data),

    update: (id: number, data: any) =>
      axiosInstance.put<BaseResponse<any>>(`/v1/topics/${id}`, data),

    toggle: (id: number) =>
      axiosInstance.patch<BaseResponse<any>>(`/v1/topics/${id}/toggle`),

    delete: (id: number) =>
      axiosInstance.delete<BaseResponse<any>>(`/v1/topics/${id}`),

    addSession: (topicId: number, data: any) =>
      axiosInstance.post<BaseResponse<any>>(`/v1/topics/${topicId}/sessions`, data),
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
        xpEarned?: number;
        level?: number;
        xp?: number;
        totalLessonsCompleted?: number;
      }>>(`/v1/student/lessons/${lessonId}/complete`, { score }),

    getCompletedLessonIds: () =>
      axiosInstance.get<BaseResponse<number[]>>('/v1/student/progress/completed-lesson-ids'),

    getCompletedSessionIds: () =>
      axiosInstance.get<BaseResponse<number[]>>('/v1/student/progress/completed-session-ids'),

    /** Sessions mà học sinh đã/đang tham gia (có ít nhất 1 bài hoàn thành). Cho Dashboard "Đang học". */
    getParticipatedSessions: () =>
      axiosInstance.get<BaseResponse<any[]>>('/v1/student/progress/participated-sessions'),

    /** Lớp mà học sinh đã ghi danh. */
    getClasses: () =>
      axiosInstance.get<BaseResponse<any[]>>('/v1/student/classes'),

    /** Bài tập giao trong lớp (chỉ lớp đã ghi danh). */
    getClassAssignments: (classId: number) =>
      axiosInstance.get<BaseResponse<any[]>>(`/v1/student/classes/${classId}/assignments`),
  },
};

export default apiService;

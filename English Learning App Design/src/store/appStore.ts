import { create } from 'zustand';

// FE core types for local UI only
// NOTE: These are now decoupled from mock data and
// are hydrated from real backend responses when available.
export interface AppUser {
  id: string;
  name: string;
  avatar?: string;
  level: number;
  xp: number;
  streak: number;
  totalLessonsCompleted: number;
}

export interface AppUserProgress {
  currentSessionId: string | null;
  currentLessonId: string | null;
  completedLessons: string[];
}

export interface AppSessionSummary {
  id: string;
  title: string;
  description: string;
  totalLessons: number;
  completedLessons: number;
  estimatedTime: number;
}

export interface AppLessonSummary {
  id: string;
  sessionId: string;
  title: string;
  duration: number;
}

interface AppState {
  user: AppUser | null;
  userProgress: AppUserProgress;
  sessions: AppSessionSummary[];
  lessons: AppLessonSummary[];
  currentView: 'home' | 'sessions' | 'session-detail' | 'lesson' | 'vocabulary' | 'profile';
  selectedSessionId: string | null;
  selectedLessonId: string | null;
  
  // Actions
  hydrateFromDashboard: (payload: {
    user: {
      id: string;
      fullName: string;
      avatarUrl?: string;
      level: number;
      xp: number;
      streak: number;
      totalLessonsCompleted: number;
    };
  }) => void;
  setCurrentView: (view: AppState['currentView']) => void;
  setSelectedSession: (sessionId: string) => void;
  setSelectedLesson: (lessonId: string) => void;
  completeLesson: (lessonId: string, score: number) => void;
  addXP: (amount: number) => void;
  incrementStreak: () => void;
}

export const useAppStore = create<AppState>((set, get) => ({
  user: null,
  userProgress: {
    currentSessionId: null,
    currentLessonId: null,
    completedLessons: [],
  },
  sessions: [],
  lessons: [],
  currentView: 'home',
  selectedSessionId: null,
  selectedLessonId: null,

  hydrateFromDashboard: ({ user }) =>
    set({
      user: {
        id: user.id,
        name: user.fullName,
        avatar: user.avatarUrl,
        level: user.level,
        xp: user.xp,
        streak: user.streak,
        totalLessonsCompleted: user.totalLessonsCompleted,
      },
    }),

  setCurrentView: (view) => set({ currentView: view }),
  
  setSelectedSession: (sessionId) => set({ 
    selectedSessionId: sessionId,
    currentView: 'session-detail'
  }),
  
  setSelectedLesson: (lessonId) => set({ 
    selectedLessonId: lessonId,
    currentView: 'lesson'
  }),
  
  completeLesson: (lessonId, score) => set((state) => {
    const updatedProgress = {
      ...state.userProgress,
      completedLessons: [...state.userProgress.completedLessons, lessonId],
      lessonScores: {
        ...state.userProgress.lessonScores,
        [lessonId]: score,
      },
    };

    const updatedLessons = state.lessons.map(lesson =>
      lesson.id === lessonId
        ? { ...lesson, status: 'completed' as const }
        : lesson
    );

    const updatedUser = {
      ...state.user,
      totalLessonsCompleted: state.user.totalLessonsCompleted + 1,
    };

    return {
      userProgress: updatedProgress,
      lessons: updatedLessons,
      user: updatedUser,
    };
  }),

  addXP: (amount) => set((state) => ({
    user: {
      ...state.user,
      xp: state.user.xp + amount,
      level: Math.floor((state.user.xp + amount) / 250) + 1,
    },
  })),

  incrementStreak: () => set((state) => ({
    user: {
      ...state.user,
      streak: state.user.streak + 1,
    },
  })),
}));

import { create } from 'zustand';
import type { User, UserProgress, Session, Lesson } from '@/types';
import { mockUser, mockUserProgress, mockSessions, mockLessons } from '@/services/mockData';

interface AppState {
  user: User;
  userProgress: UserProgress;
  sessions: Session[];
  lessons: Lesson[];
  currentView: 'home' | 'sessions' | 'session-detail' | 'lesson' | 'vocabulary' | 'profile';
  selectedSessionId: string | null;
  selectedLessonId: string | null;
  
  // Actions
  setCurrentView: (view: AppState['currentView']) => void;
  setSelectedSession: (sessionId: string) => void;
  setSelectedLesson: (lessonId: string) => void;
  completeLesson: (lessonId: string, score: number) => void;
  addXP: (amount: number) => void;
  incrementStreak: () => void;
}

export const useAppStore = create<AppState>((set, get) => ({
  user: mockUser,
  userProgress: mockUserProgress,
  sessions: mockSessions,
  lessons: mockLessons,
  currentView: 'home',
  selectedSessionId: null,
  selectedLessonId: null,

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

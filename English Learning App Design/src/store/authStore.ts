import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthUser, UserRole, LoginCredentials } from '@/types/auth';

interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  
  // Actions
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => void;
  register: (name: string, email: string, password: string, role: UserRole) => Promise<void>;
  updateUser: (updates: Partial<AuthUser>) => void;
  checkPermission: (permission: string) => boolean;
}

// Mock login function - Replace with real API call
const mockLogin = async (email: string, password: string): Promise<AuthUser> => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 500));
  
  // Mock users for different roles
  if (email.includes('student')) {
    return {
      id: 'student-1',
      name: 'John Student',
      email: email,
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=John',
      role: 'student',
      permissions: ['view:lessons', 'submit:homework'],
      level: 3,
      xp: 750,
      streak: 5,
      totalLessonsCompleted: 15,
      faceIdEnrolled: false,
      enrolledClasses: ['class-1', 'class-2'], // Student is enrolled in 2 classes
    };
  } else if (email.includes('teacher')) {
    return {
      id: 'teacher-1',
      name: 'Jane Teacher',
      email: email,
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jane',
      role: 'teacher',
      permissions: ['view:lessons', 'create:lessons', 'manage:classes', 'view:analytics'],
      teachingSubjects: ['English', 'Pronunciation'],
    };
  } else if (email.includes('admin')) {
    return {
      id: 'admin-1',
      name: 'Admin User',
      email: email,
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin',
      role: 'admin',
      permissions: ['*'], // All permissions
      isSuperAdmin: true,
    };
  }
  
  throw new Error('Invalid credentials');
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,

      login: async (credentials) => {
        set({ isLoading: true });
        try {
          const user = await mockLogin(credentials.email, credentials.password);
          set({ user, isAuthenticated: true, isLoading: false });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      logout: () => {
        set({ user: null, isAuthenticated: false });
      },

      register: async (name, email, password, role) => {
        set({ isLoading: true });
        try {
          // Simulate registration
          await new Promise(resolve => setTimeout(resolve, 500));
          
          const user: AuthUser = {
            id: `${role}-${Date.now()}`,
            name,
            email,
            role,
            permissions: role === 'student' 
              ? ['view:lessons', 'submit:homework']
              : role === 'teacher'
              ? ['view:lessons', 'create:lessons', 'manage:classes']
              : ['*'],
          };
          
          set({ user, isAuthenticated: true, isLoading: false });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      updateUser: (updates) => {
        const currentUser = get().user;
        if (currentUser) {
          set({ user: { ...currentUser, ...updates } });
        }
      },

      checkPermission: (permission) => {
        const user = get().user;
        if (!user) return false;
        if (user.permissions.includes('*')) return true;
        return user.permissions.includes(permission);
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ 
        user: state.user, 
        isAuthenticated: state.isAuthenticated 
      }),
    }
  )
);
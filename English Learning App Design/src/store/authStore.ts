/**
 * Authentication Store using Zustand with persistence
 * Handles user authentication state, tokens, and permissions
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthUser, UserRole, LoginCredentials } from '@/types/auth';
import axiosInstance from '@/services/axiosConfig';
import { API_BASE_URL } from '@/services/baseUrl';
import { logError } from '@/utils/errorHandler';

interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

interface AuthState {
  user: AuthUser | null;
  tokens: AuthTokens | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  // Actions
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => void;
  register: (name: string, email: string, password: string, role: UserRole) => Promise<void>;
  updateUser: (updates: Partial<AuthUser>) => void;
  updateTokens: (tokens: AuthTokens) => void;
  checkPermission: (permission: string) => boolean;
}

// Helper function to normalize role from backend
const normalizeRole = (backendRole: string): UserRole => {
  const raw = (backendRole ?? '').toString().toLowerCase();

  // Direct matches (e.g. FE-created accounts)
  if (raw === 'student' || raw === 'role_user' || raw === 'user') return 'student';
  if (raw === 'teacher' || raw === 'role_teacher') return 'teacher';
  if (raw === 'admin' || raw === 'role_admin') return 'admin';

  // Fallback: contains-based (handles variants like "ROLE_STUDENT", "STUDENT", etc.)
  if (raw.includes('teacher')) return 'teacher';
  if (raw.includes('admin')) return 'admin';
  return 'student';
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      tokens: null,
      isAuthenticated: false,
      isLoading: false,

      // LOGIN: Call BE /v1/auth/login, receive LoginResponse (user + token + refreshToken)
      login: async (credentials) => {
        set({ isLoading: true });
        try {
          const res = await axiosInstance.post('/v1/auth/login', {
            email: credentials.email,
            password: credentials.password,
          });

          const body = res.data;
          const data = body.data as {
            user: any;
            token: string;
            refreshToken: string;
          };

          const backendRole = (data.user.role ?? '').toString();
          const normalizedRole = normalizeRole(backendRole);

          const mappedUser: AuthUser = {
            id: data.user.id?.toString() ?? '',
            name: data.user.fullName ?? data.user.name ?? data.user.email,
            email: data.user.email,
            avatar: data.user.avatarUrl ?? undefined,
            role: normalizedRole,
            permissions: Array.isArray(data.user.permissions) && data.user.permissions.length > 0
              ? data.user.permissions
              : ['view:lessons'],
            // Student specific fields
            level: data.user.level,
            xp: data.user.xp,
            streak: data.user.streak,
            totalLessonsCompleted: data.user.totalLessonsCompleted,
            faceIdEnrolled: data.user.faceIdEnrolled,
            enrolledClasses: data.user.enrolledClasses,
            // Teacher specific fields
            teachingSubjects: data.user.teachingSubjects,
            // Admin specific fields
            isSuperAdmin: data.user.isSuperAdmin,
          };

          set({
            user: mappedUser,
            tokens: {
              accessToken: data.token,
              refreshToken: data.refreshToken,
            },
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error) {
          set({ isLoading: false });
          logError('AuthStore.login', error, { email: credentials.email });
          throw error;
        }
      },

      // LOGOUT: Call BE (if has token) then clear user + token on FE
      logout: () => {
        const { tokens } = get();

        if (tokens?.accessToken) {
          // Fire-and-forget, don't block UI
          axiosInstance
            .post('/v1/auth/logout')
            .catch(() => {
              // Ignore logout errors, FE still clears state
            });
        }

        set({ user: null, tokens: null, isAuthenticated: false });
      },

      // REGISTER: Call /v1/auth/register/local, then auto-login
      register: async (name, email, password, role) => {
        console.log('--- AuthStore: register start ---', { name, email, role });
        set({ isLoading: true });
        try {
          const payload = {
            fullName: name,
            email,
            password,
            role: role.toUpperCase(), // STUDENT / TEACHER
          };
          console.log('--- AuthStore: Sending request to:', `${API_BASE_URL}/v1/auth/register/local`);
          console.log('--- AuthStore: Payload:', payload);

          await axiosInstance.post('/v1/auth/register/local', payload);

          console.log('--- AuthStore: Register success, now logging in...');
          // Auto-login after registration for better UX
          await get().login({ email, password });
          set({ isLoading: false });
        } catch (error) {
          set({ isLoading: false });
          logError('AuthStore.register', error, { email, role });
          throw error;
        }
      },

      updateUser: (updates) => {
        const currentUser = get().user;
        if (currentUser) {
          set({ user: { ...currentUser, ...updates } });
        }
      },

      updateTokens: (tokens) => {
        set({ tokens });
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
        tokens: state.tokens,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
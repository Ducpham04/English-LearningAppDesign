/**
 * Axios Configuration with Interceptors
 * Handles automatic token attachment, refresh token logic, and error handling
 */

import axios, { AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { useAuthStore } from '@/store/authStore';

// API Base URL
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance
export const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 seconds timeout
  headers: {
    'Content-Type': 'application/json',
  },
});

// Track ongoing refresh requests to prevent multiple simultaneous refreshes
let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

/**
 * Add subscriber to wait for token refresh
 */
function subscribeTokenRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb);
}

/**
 * Notify all subscribers with new token
 */
function onTokenRefreshed(token: string) {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
}

/**
 * Request Interceptor
 * Automatically attaches JWT token to all requests
 */
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Get auth state from Zustand store
    const state = useAuthStore.getState();

    // If we have a token, attach it to Authorization header
    if (state.tokens?.accessToken && config.headers) {
      config.headers.Authorization = `Bearer ${state.tokens.accessToken}`;
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

/**
 * Response Interceptor
 * Handles 401 errors and automatic token refresh
 */
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Handle 401 Unauthorized errors
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      // Mark request as retried to prevent infinite loops
      originalRequest._retry = true;

      // Get auth state
      const authStore = useAuthStore.getState();

      // If we don't have a refresh token, logout and redirect to login
      if (!authStore.tokens?.refreshToken) {
        authStore.logout();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      // If already refreshing, queue this request
      if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            resolve(axiosInstance(originalRequest));
          });
        });
      }

      // Start refresh process
      isRefreshing = true;

      try {
        // Call refresh token endpoint
        const response = await axios.post(`${API_BASE_URL}/v1/auth/refresh`, {
          refreshToken: authStore.tokens.refreshToken,
        });

        const data = response.data.data;
        const newAccessToken = data.token;
        const newRefreshToken = data.refreshToken;

        // Update auth store with new tokens
        authStore.updateTokens({
          accessToken: newAccessToken,
          refreshToken: newRefreshToken,
        });

        // Notify all waiting requests
        onTokenRefreshed(newAccessToken);

        // Retry original request with new token
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        }

        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // If refresh fails, logout user
        authStore.logout();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // Handle other errors
    return Promise.reject(error);
  }
);

export default axiosInstance;
/**
 * Axios Configuration with Interceptors
 * Handles automatic token attachment, refresh token logic, and error handling
 */

import axios, { AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { useAuthStore } from '@/store/authStore';
import { API_BASE_URL } from './baseUrl';

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
let logoutInitiated = false;

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
 * Handle logout redirect once to prevent multiple redirects
 */
function handleLogoutRedirect() {
  if (!logoutInitiated && !window.location.pathname.includes('/login')) {
    logoutInitiated = true;
    console.warn('[Axios] Initiating logout redirect...');
    setTimeout(() => {
      window.location.href = '/login';
    }, 500);
  }
}

/**
 * Request Interceptor
 * Automatically attaches JWT token to all requests
 */
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Log request details
    console.log(`[Axios Request] ${config.method?.toUpperCase()} ${config.baseURL}${config.url}`, {
      url: config.url,
      baseURL: config.baseURL,
      fullURL: `${config.baseURL}${config.url}`,
      headers: config.headers,
    });

    // Get auth state from Zustand store
    const state = useAuthStore.getState();

    // If we have a token, attach it to Authorization header
    if (state.tokens?.accessToken && config.headers) {
      config.headers.Authorization = `Bearer ${state.tokens.accessToken}`;
      console.log('[Axios Request] Token attached to request');
    } else {
      console.log('[Axios Request] No token available');
    }

    return config;
  },
  (error: AxiosError) => {
    console.error('[Axios Request Error]', error);
    return Promise.reject(error);
  }
);

/**
 * Response Interceptor
 * Handles 401 errors and automatic token refresh
 */
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log(`[Axios Response] ${response.status} - ${response.config.method?.toUpperCase()} ${response.config.url}`, {
      data: response.data,
    });
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    
    console.error('[Axios Response Error]', {
      status: error.response?.status,
      url: error.config?.url,
      message: error.message,
      data: (error.response as any)?.data,
    });

    // Handle 401 Unauthorized errors
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      console.warn('[Axios 401] Unauthorized error detected');
      // Mark request as retried to prevent infinite loops
      originalRequest._retry = true;

      // Get auth state
      const authStore = useAuthStore.getState();

      // If we don't have a refresh token, logout and redirect to login
      if (!authStore.tokens?.refreshToken) {
        console.warn('[Axios 401] No refresh token available, logging out...');
        authStore.logout();
        handleLogoutRedirect();
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
        console.log('[Axios 401] Attempting to refresh token...');
        const response = await axios.post(`${API_BASE_URL}/v1/auth/refresh`, {
          refreshToken: authStore.tokens.refreshToken,
        });

        const data = response.data.data;
        const newAccessToken = data.token;
        const newRefreshToken = data.refreshToken;

        console.log('[Axios 401] Token refreshed successfully');

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
        console.error('[Axios 401] Token refresh failed, logging out...', refreshError);
        authStore.logout();
        handleLogoutRedirect();
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
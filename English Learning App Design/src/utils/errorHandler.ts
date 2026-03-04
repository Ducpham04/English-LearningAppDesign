/**
 * Error Handler Utility
 * Provides consistent error message extraction from various error sources
 */

import type { AxiosError } from 'axios';

/**
 * Extract a user-friendly error message from various error sources
 * Handles Backend BaseResponse structure:
 * {
 *   "success": false,
 *   "message": "Error message here",
 *   "errorCode": "ERROR_CODE",
 *   ...
 * }
 */
export function getErrorMessage(error: any, defaultMessage: string = 'An error occurred'): string {
  // If it's an axios error with response data
  if (error?.response?.data) {
    const data = error.response.data;

    // Check for message at root level (from BaseResponse)
    if (typeof data.message === 'string' && data.message.trim()) {
      return data.message;
    }

    // Check for message nested in data object
    if (typeof data.data === 'object' && data.data?.message) {
      return data.data.message;
    }

    // Check for error field
    if (typeof data.error === 'string' && data.error.trim()) {
      return data.error;
    }
  }

  // Check for error message at root level
  if (typeof error?.message === 'string' && error.message.trim()) {
    return error.message;
  }

  // Return default message
  return defaultMessage;
}

/**
 * Extract error code from response
 */
export function getErrorCode(error: any): string | null {
  if (error?.response?.data?.errorCode) {
    return error.response.data.errorCode;
  }
  return null;
}

/**
 * Check if error is a specific type
 */
export function isErrorCode(error: any, code: string): boolean {
  return getErrorCode(error) === code;
}

/**
 * Extract detailed error info
 */
export function extractErrorInfo(error: any) {
  return {
    message: getErrorMessage(error),
    code: getErrorCode(error),
    status: error?.response?.status,
    fullResponse: error?.response?.data,
  };
}

/**
 * Log error with consistent format
 */
export function logError(source: string, error: any, context?: Record<string, any>) {
  const errorInfo = extractErrorInfo(error);
  console.error(`[${source}] Error:`, {
    message: errorInfo.message,
    code: errorInfo.code,
    status: errorInfo.status,
    context,
    fullError: error,
  });
}


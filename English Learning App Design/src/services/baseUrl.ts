// API Base URL - Single source of truth
// In dev: use /api proxy (configured in vite.config.ts)
// In production: use full VITE_API_BASE_URL env variable
const isDev = import.meta.env.DEV;
const envUrl = import.meta.env.VITE_API_BASE_URL;

console.log('[baseUrl] Environment:', {
  isDev,
  envUrl,
  mode: import.meta.env.MODE,
});

function normalizeApiBaseUrl(input: string | undefined): string {
  const fallback = '/api';
  if (!input) return fallback;

  const trimmed = input.trim().replace(/\/+$/, '');
  if (!trimmed) return fallback;

  // If env provides a relative base like "/api" (recommended), keep it.
  if (trimmed.startsWith('/')) return trimmed;

  // If env provides absolute origin, ensure it includes "/api" context-path.
  // Example: "http://localhost:8080" -> "http://localhost:8080/api"
  if (trimmed.endsWith('/api')) return trimmed;
  return `${trimmed}/api`;
}

export const API_BASE_URL = isDev ? '/api' : normalizeApiBaseUrl(envUrl);

console.log('[baseUrl] API_BASE_URL:', API_BASE_URL);

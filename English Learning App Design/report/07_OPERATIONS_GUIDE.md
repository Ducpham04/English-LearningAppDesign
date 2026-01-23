# 🔧 Operations & Maintenance Guide

**Version**: 1.0.0  
**Last Updated**: 23/01/2026

---

## Table of Contents

1. [Getting Started](#1-getting-started)
2. [Development Workflow](#2-development-workflow)
3. [Build & Deployment](#3-build--deployment)
4. [Performance Optimization](#4-performance-optimization)
5. [Monitoring & Logging](#5-monitoring--logging)
6. [Troubleshooting](#6-troubleshooting)
7. [Database Management](#7-database-management)
8. [Security Checklist](#8-security-checklist)

---

## 1. Getting Started

### Initial Setup

```bash
# Clone repository
git clone <repository-url>
cd "English Learning App Design"

# Install dependencies
npm install

# Create environment files
cp .env.example .env.development
cp .env.example .env.production

# Edit environment variables
# .env.development
VITE_API_BASE_URL=http://localhost:3000/api/v1
VITE_DEBUG=true

# .env.production
VITE_API_BASE_URL=https://api.english-ai.com/v1
VITE_DEBUG=false
```

### Verify Installation

```bash
# Check Node.js version
node --version  # Should be 18.x or higher

# Check npm version
npm --version

# Run development server
npm run dev

# Output should show:
# VITE v6.3.5  ready in XXX ms
# ➜  Local:   http://localhost:5173/
# ➜  press h to show help
```

---

## 2. Development Workflow

### Daily Development

```bash
# 1. Start development server
npm run dev

# 2. Development server will:
#    - Watch for file changes
#    - Auto-reload on save (HMR)
#    - Show compilation errors in terminal
#    - Display console logs in terminal

# 3. Open browser
# http://localhost:5173

# 4. Test login credentials
# Student: student@demo.com / any password
# Teacher: teacher@demo.com / any password
# Admin: admin@demo.com / any password
```

### Code Structure Conventions

#### Component Creation
```typescript
// src/features/[feature]/ComponentName.tsx

import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import type { ComponentProps } from '@/types';

export function ComponentName() {
  const navigate = useNavigate();
  const { user } = useAuthStore();

  const handleAction = useCallback(() => {
    // Handle action
  }, []);

  return (
    <div className="...">
      {/* Component content */}
    </div>
  );
}

export default ComponentName;
```

#### Service Creation
```typescript
// src/services/newService.ts

import apiClient from './apiClient';
import type { ApiResponse, DataType } from '@/types';

class NewService {
  async getAll(): Promise<ApiResponse<DataType[]>> {
    return (await apiClient.get('/endpoint')).data;
  }

  async getById(id: string): Promise<ApiResponse<DataType>> {
    return (await apiClient.get(`/endpoint/${id}`)).data;
  }

  async create(data: Partial<DataType>): Promise<ApiResponse<DataType>> {
    return (await apiClient.post('/endpoint', data)).data;
  }

  async update(id: string, data: Partial<DataType>): Promise<ApiResponse<DataType>> {
    return (await apiClient.put(`/endpoint/${id}`, data)).data;
  }

  async delete(id: string): Promise<ApiResponse<void>> {
    return (await apiClient.delete(`/endpoint/${id}`)).data;
  }
}

export const newService = new NewService();
```

#### Type Definition
```typescript
// src/types/newTypes.ts

export interface NewType {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface NewTypeInput {
  name: string;
  description: string;
}

// Export in src/types/index.ts
export * from './newTypes';
```

### Commit Convention

```bash
# Feature branch
git checkout -b feature/feature-name

# Commit messages
git commit -m "feat: Add new feature"
git commit -m "fix: Fix bug in component"
git commit -m "refactor: Improve code structure"
git commit -m "docs: Update documentation"
git commit -m "style: Format code"
git commit -m "test: Add tests"

# Push and create PR
git push origin feature/feature-name
```

---

## 3. Build & Deployment

### Development Build

```bash
# Start development server with HMR
npm run dev

# This will:
# - Start server at http://localhost:5173
# - Enable Hot Module Replacement
# - Show errors in browser
# - Serve unminified code for debugging
```

### Production Build

```bash
# Build for production
npm run build

# Output:
# dist/
# ├── index.html
# ├── assets/
# │   ├── index-xxx.js
# │   ├── index-xxx.css
# │   └── ...
# └── ...

# Check build size
ls -lh dist/

# Expected: ~200-300KB gzipped
```

### Preview Production Build

```bash
# Build and preview
npm run build
npm run preview

# This will:
# - Serve the production build locally
# - Show how it will look in production
# - Use production URLs and settings
```

### Deployment Strategies

#### Option 1: Vercel (Recommended)

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel

# Configure:
# - Framework: Vite
# - Build Command: npm run build
# - Output Directory: dist
# - Install Command: npm install

# Environment variables in Vercel dashboard
VITE_API_BASE_URL=https://api.english-ai.com/v1
```

#### Option 2: Netlify

```bash
# Install Netlify CLI
npm i -g netlify-cli

# Deploy
netlify deploy

# Or use GitHub integration for auto-deploy
```

#### Option 3: Docker

```dockerfile
# Dockerfile
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Serve with nginx
FROM nginx:alpine
COPY --from=0 /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
```

```bash
# Build and run
docker build -t english-learning-app .
docker run -p 80:80 english-learning-app
```

---

## 4. Performance Optimization

### Monitoring Performance

#### Lighthouse Audit
```bash
# Chrome DevTools -> Lighthouse
# Or use CLI:
npm install -g @lighthouse-cli

lighthouse https://your-domain.com --view
```

#### Bundle Size Analysis

```bash
# Install analyzer
npm install -D vite-bundle-visualizer

# Update vite.config.ts
import visualizer from 'vite-bundle-visualizer';

export default defineConfig({
  plugins: [
    visualizer({
      open: true,
    }),
  ],
});

# Build and view
npm run build
```

### Optimization Techniques

#### 1. Code Splitting
```typescript
// Already configured in routes
import { lazy, Suspense } from 'react';

const Dashboard = lazy(() => import('@/pages/Dashboard'));

function App() {
  return (
    <Suspense fallback={<Loading />}>
      <Dashboard />
    </Suspense>
  );
}
```

#### 2. Image Optimization
```typescript
// Recommended: Use image optimization service
// Option 1: Build-time optimization
import imageCompression from 'browser-image-compression';

async function compressImage(file: File) {
  const options = {
    maxSizeMB: 1,
    maxWidthOrHeight: 1920,
    useWebWorker: true,
  };
  return imageCompression(file, options);
}

// Option 2: Use CDN with automatic optimization
// e.g., Cloudinary, Imgix
const imageUrl = `https://cdn.example.com/images/${id}?w=800&q=80`;
```

#### 3. Component Memoization
```typescript
import { memo, useMemo, useCallback } from 'react';

// Memoize expensive components
const MemoizedList = memo(function List({ items }: Props) {
  return items.map(item => <Item key={item.id} item={item} />);
});

// Memoize expensive computations
function DataProcessor({ data }: Props) {
  const processed = useMemo(() => {
    return expensiveOperation(data);
  }, [data]);

  return <div>{processed}</div>;
}

// Memoize callbacks
const handleClick = useCallback(() => {
  doSomething();
}, []);
```

#### 4. Lazy Loading
```typescript
// Intersection Observer for lazy loading
import { useInView } from 'react-intersection-observer';

function LazyComponent() {
  const { ref, inView } = useInView();

  return (
    <div ref={ref}>
      {inView && <ExpensiveComponent />}
    </div>
  );
}
```

### Caching Strategy

```typescript
// HTTP response caching
const apiClient = axios.create();

// Cache GET requests for 5 minutes
apiClient.interceptors.response.use(response => {
  if (response.config.method === 'GET') {
    const cacheKey = response.config.url;
    sessionStorage.setItem(cacheKey, JSON.stringify(response.data));
  }
  return response;
});

// Try cache before API call
apiClient.interceptors.request.use(config => {
  if (config.method === 'GET') {
    const cached = sessionStorage.getItem(config.url);
    if (cached) {
      return Promise.resolve({
        data: JSON.parse(cached),
        status: 200,
        statusText: 'OK',
      });
    }
  }
  return config;
});
```

---

## 5. Monitoring & Logging

### Client-Side Monitoring

#### Setup Error Tracking (Sentry)

```bash
npm install @sentry/react
```

```typescript
// main.tsx
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: "https://xxx@xxx.ingest.sentry.io/xxx",
  environment: process.env.NODE_ENV,
  tracesSampleRate: 1.0,
});

// Wrap App component
const SentryRoutes = Sentry.withSentryRouting(App);
```

#### Console Logging

```typescript
// Create logger utility
// src/utils/logger.ts

export const logger = {
  debug: (message: string, data?: any) => {
    if (process.env.NODE_ENV === 'development') {
      console.log(`[DEBUG] ${message}`, data);
    }
  },
  
  info: (message: string, data?: any) => {
    console.log(`[INFO] ${message}`, data);
  },
  
  warn: (message: string, data?: any) => {
    console.warn(`[WARN] ${message}`, data);
  },
  
  error: (message: string, error?: Error) => {
    console.error(`[ERROR] ${message}`, error);
    // Also send to error tracking service
    Sentry.captureException(error);
  },
};

// Usage
import { logger } from '@/utils/logger';

logger.debug('Component mounted');
logger.info('User logged in', { userId: '123' });
logger.warn('API response slower than expected');
logger.error('Failed to load data', error);
```

### Server-Side Monitoring

#### Database Monitoring

```bash
# For PostgreSQL
# Use built-in monitoring tools or services like:
# - pgAdmin for management
# - New Relic for monitoring
# - DataDog for observability
```

#### API Performance Monitoring

```bash
# Install APM tool
npm install newrelic

# Or use built-in Express middleware
import { logger } from 'morgan';

app.use(logger('combined'));

app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    console.log(`${req.method} ${req.path} ${res.statusCode} ${duration}ms`);
  });
  next();
});
```

---

## 6. Troubleshooting

### Common Issues

#### Issue 1: Dev Server Won't Start
```bash
# Check if port is in use
lsof -i :5173

# Kill process
kill -9 <PID>

# Or use different port
npm run dev -- --port 3000
```

#### Issue 2: Module Not Found Error
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear Vite cache
rm -rf .vite
npm run dev
```

#### Issue 3: TypeScript Errors
```bash
# Check TypeScript version
npx tsc --version

# Fix type errors
npx tsc --noEmit

# Generate type definitions
npx tsc --declaration --emitDeclarationOnly
```

#### Issue 4: Slow Build Time
```bash
# Check build time
npm run build -- --debug

# Analyze bundle
npm run build:analyze

# Recommended fixes:
# - Code split large chunks
# - Remove unused dependencies
# - Use production mode
# - Enable minification
```

---

## 7. Database Management

### Schema Management

```sql
-- Create main tables
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255),
  role VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classes (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  teacher_id UUID REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lessons (
  id UUID PRIMARY KEY,
  title VARCHAR(255),
  content TEXT,
  class_id UUID REFERENCES classes(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE progress (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  lesson_id UUID REFERENCES lessons(id),
  completed BOOLEAN DEFAULT false,
  score INT,
  completed_at TIMESTAMP
);
```

### Backup Strategy

```bash
# PostgreSQL backup
pg_dump -U username -d database_name > backup.sql

# Automated daily backups
0 2 * * * pg_dump -U username -d database_name | gzip > /backups/db-$(date +%Y%m%d).sql.gz

# Restore from backup
psql -U username -d database_name < backup.sql
```

---

## 8. Security Checklist

### Before Production

- [ ] Remove debug mode from production build
- [ ] Set secure headers (HTTPS, CSP, HSTS)
- [ ] Implement rate limiting on API
- [ ] Validate all user inputs
- [ ] Use HTTPS for all communications
- [ ] Store secrets in environment variables
- [ ] Implement CORS properly
- [ ] Use secure authentication (JWT with short expiry)
- [ ] Enable CSRF protection
- [ ] Sanitize HTML content
- [ ] Implement logging and monitoring
- [ ] Regular security audits
- [ ] Update dependencies regularly
- [ ] Implement rate limiting
- [ ] Use Web Application Firewall (WAF)

### Security Headers (Express.js)

```typescript
import helmet from 'helmet';

app.use(helmet());
app.use(helmet.hsts({ maxAge: 31536000 }));
app.use(helmet.contentSecurityPolicy({
  directives: {
    defaultSrc: ["'self'"],
    scriptSrc: ["'self'", "'unsafe-inline'"],
    styleSrc: ["'self'", "'unsafe-inline'"],
  },
}));
```

### SQL Injection Prevention

```typescript
// Use parameterized queries
// ❌ Wrong
const query = `SELECT * FROM users WHERE email = '${email}'`;

// ✅ Correct
const query = 'SELECT * FROM users WHERE email = $1';
db.query(query, [email]);
```

### Input Validation

```typescript
import { z } from 'zod';

const userSchema = z.object({
  email: z.string().email(),
  password: z.string().min(6),
  name: z.string().min(1),
});

function validateInput(data: unknown) {
  try {
    return userSchema.parse(data);
  } catch (error) {
    throw new Error('Invalid input');
  }
}
```

---

## Deployment Checklist

### Pre-Deployment

- [ ] Run all tests
- [ ] Check code quality (ESLint, Prettier)
- [ ] Update version number
- [ ] Review all recent changes
- [ ] Test on production-like environment
- [ ] Verify all environment variables
- [ ] Check database migrations
- [ ] Review security checklist
- [ ] Create deployment plan
- [ ] Test rollback procedure

### Post-Deployment

- [ ] Verify application is running
- [ ] Check error logs
- [ ] Monitor performance metrics
- [ ] Test user flows
- [ ] Verify data integrity
- [ ] Check uptime monitoring
- [ ] Review analytics

### Monitoring Alerts

```typescript
// Set up alerts for:
// - High error rate (> 5% of requests)
// - Slow API response (> 2s)
// - Database connection failures
// - Memory usage > 80%
// - CPU usage > 90%
// - Disk usage > 85%
```

---

## Maintenance Schedule

### Daily
- Monitor error logs
- Check system health
- Review new user reports

### Weekly
- Review analytics
- Update dependencies (non-major)
- Backup database
- Performance review

### Monthly
- Security audit
- Code review
- Update major dependencies
- Performance optimization
- Disaster recovery drill

### Quarterly
- Full system audit
- User feedback review
- Technology stack review
- Capacity planning

---

*Generated on 23/01/2026*

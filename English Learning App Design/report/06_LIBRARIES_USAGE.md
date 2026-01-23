# 📚 Libraries & Dependencies Usage Guide

**Version**: 1.0.0  
**Total Dependencies**: 40+ major packages  
**Last Updated**: 23/01/2026

---

## Table of Contents

1. [Core Libraries](#1-core-libraries)
2. [UI Component Libraries](#2-ui-component-libraries)
3. [Styling & Animation](#3-styling--animation)
4. [Form Management](#4-form-management)
5. [Data Management & Visualization](#5-data-management--visualization)
6. [Utilities & Helpers](#6-utilities--helpers)
7. [Recommended Additions](#7-recommended-additions)
8. [Version Management](#8-version-management)
9. [Dependency Updates](#9-dependency-updates)

---

## 1. Core Libraries

### React 18.3.1
**Purpose**: UI library and component framework

**Current Usage**:
- Main application framework
- Component-based architecture
- Hooks for state and side effects
- Context API for global state (where needed)

**Key Features Used**:
```typescript
import { useState, useEffect, useCallback } from 'react';
import { Suspense, lazy } from 'react';

// Functional components with hooks
function MyComponent() {
  const [state, setState] = useState(initialValue);
  
  useEffect(() => {
    // Side effects
  }, [dependencies]);

  return <div>Component</div>;
}

// Lazy loading routes
const AdminDashboard = lazy(() => import('@/features/admin/AdminDashboard'));

// Suspense for async components
<Suspense fallback={<Loading />}>
  <AdminDashboard />
</Suspense>
```

**Installation**: Already included (peer dependency)

---

### TypeScript (Latest)
**Purpose**: Type safety and development experience

**Current Usage**:
- Type definitions for all components
- Interface definitions for data types
- Generic types for reusable components
- Type guards and narrowing

**Example Usage**:
```typescript
import type { ReactNode, FC } from 'react';

interface ComponentProps {
  title: string;
  children: ReactNode;
  onClose?: () => void;
}

const MyComponent: FC<ComponentProps> = ({ title, children, onClose }) => {
  return <div>{children}</div>;
};
```

---

### Vite 6.3.5
**Purpose**: Build tool and development server

**Current Usage**:
- Fast dev server with HMR (Hot Module Replacement)
- Optimized production builds
- Asset handling
- Environment variables

**Config Files**:
- `vite.config.ts` - Main configuration
- `.env.development` - Dev environment variables
- `.env.production` - Production variables

**Common Commands**:
```bash
npm run dev          # Start development server
npm run build        # Production build
npm run preview      # Preview production build
```

---

### Zustand 5.0.10
**Purpose**: Lightweight state management

**Current Implementation**:
```typescript
// src/store/authStore.ts
import { create } from 'zustand';

interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  login: (user: User) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  user: null,
  login: (user) => set({ isAuthenticated: true, user }),
  logout: () => set({ isAuthenticated: false, user: null }),
}));

// Usage in components
function MyComponent() {
  const { user, logout } = useAuthStore();
  
  return (
    <div>
      <p>{user?.name}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
}
```

**Stores Created**:
- `authStore.ts` - Authentication state
- `appStore.ts` - Global app state
- `gamificationStore.ts` - Gamification state

---

### React Router 7.12.0
**Purpose**: Client-side routing

**Current Setup**:
```typescript
// src/app/RouterSetup.tsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ProtectedRoute } from './components/layout/ProtectedRoute';

export function RouterSetup() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        
        {/* Protected routes */}
        <Route
          path="/dashboard"
          element={<ProtectedRoute><Dashboard /></ProtectedRoute>}
        />
      </Routes>
    </BrowserRouter>
  );
}
```

**Features Used**:
- Route definitions
- Protected routes with role-based access
- Lazy loading with `Suspense`
- Route parameters
- Navigation with `useNavigate()`

---

## 2. UI Component Libraries

### Radix UI
**Purpose**: Accessible, unstyled component primitives

**Installed Components** (30+):
```json
{
  "@radix-ui/react-accordion": "Dialog, collapsible sections",
  "@radix-ui/react-alert-dialog": "Confirmations",
  "@radix-ui/react-dialog": "Modals",
  "@radix-ui/react-dropdown-menu": "Dropdown menus",
  "@radix-ui/react-tabs": "Tabbed interfaces",
  "@radix-ui/react-select": "Select dropdowns",
  "@radix-ui/react-checkbox": "Checkboxes",
  "@radix-ui/react-radio-group": "Radio buttons",
  "@radix-ui/react-progress": "Progress bars",
  "@radix-ui/react-slider": "Sliders",
  "@radix-ui/react-scroll-area": "Scrollable areas",
  "@radix-ui/react-tooltip": "Tooltips"
}
```

**Usage Example**:
```typescript
import * as Dialog from '@radix-ui/react-dialog';

export function MyDialog() {
  return (
    <Dialog.Root>
      <Dialog.Trigger>Open Dialog</Dialog.Trigger>
      <Dialog.Portal>
        <Dialog.Overlay />
        <Dialog.Content>
          <Dialog.Title>Dialog Title</Dialog.Title>
          <Dialog.Description>Dialog content</Dialog.Description>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  );
}
```

**Benefits**:
- Fully accessible (WCAG compliant)
- Unstyled - full customization with Tailwind
- Highly composable
- No external dependencies

---

### Material-UI (MUI)
**Purpose**: Pre-built, styled components

**Installed Packages**:
```json
{
  "@mui/material": "7.3.5",
  "@mui/icons-material": "7.3.5"
}
```

**Components Used**:
- Buttons
- Cards
- Dialogs/Modals
- Data Grid
- Snackbar
- Icons (1000+ available)

**Usage Example**:
```typescript
import {
  Button,
  Card,
  CardContent,
  Dialog,
  TextField,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

export function MyCard() {
  return (
    <Card>
      <CardContent>
        <TextField label="Username" />
        <Button
          variant="contained"
          color="primary"
          endIcon={<DeleteIcon />}
        >
          Delete
        </Button>
      </CardContent>
    </Card>
  );
}
```

---

### Lucide React
**Purpose**: SVG icon library

**Current Usage**:
```typescript
import {
  Menu,
  ChevronDown,
  LogOut,
  Settings,
  Home,
  BookOpen,
  Award,
} from 'lucide-react';

export function NavBar() {
  return (
    <nav>
      <Menu size={24} />
      <Home size={20} />
      <BookOpen size={20} />
      <Award size={20} />
    </nav>
  );
}
```

**Features**:
- 1000+ icons
- Customizable size, color, stroke width
- Tree-shakeable (only used icons in bundle)
- Consistent design

---

## 3. Styling & Animation

### Tailwind CSS 4.1.12
**Purpose**: Utility-first CSS framework

**Current Setup**:
```css
/* src/styles/tailwind.css */
@import "tailwindcss";

/* Custom utilities */
@layer components {
  .btn-primary {
    @apply px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700;
  }
}
```

**Usage Example**:
```tsx
function Button() {
  return (
    <button className="
      px-4 py-2 
      bg-green-500 hover:bg-green-600
      text-white rounded-lg
      transition-colors duration-200
      disabled:opacity-50 disabled:cursor-not-allowed
    ">
      Click Me
    </button>
  );
}
```

**Design System (Configured)**:
- Colors: Green (Student), Blue (Teacher), Purple (Admin)
- Spacing: Standard Tailwind scale
- Typography: Custom font sizes
- Shadows: Predefined levels
- Border radius: Consistent values

---

### Motion 12.23.24
**Purpose**: Smooth animations and transitions

**Usage Example**:
```typescript
import { motion } from 'motion/react';

export function AnimatedCard() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Card>Card Content</Card>
    </motion.div>
  );
}
```

**Common Animations**:
- Fade in/out
- Slide animations
- Scale animations
- Shared layout animations

---

### Emotion (@emotion/react, @emotion/styled)
**Purpose**: CSS-in-JS for dynamic styling

**Usage Example**:
```typescript
import styled from '@emotion/styled';

const StyledButton = styled.button`
  background-color: ${props => props.primary ? 'blue' : 'gray'};
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
  }
`;

export function MyButton() {
  return <StyledButton primary>Click Me</StyledButton>;
}
```

---

## 4. Form Management

### React Hook Form 7.55.0
**Purpose**: Flexible form handling with validation

**Setup Example**:
```typescript
import { useForm } from 'react-hook-form';

interface FormData {
  email: string;
  password: string;
  remember: boolean;
}

export function LoginForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>();

  const onSubmit = (data: FormData) => {
    console.log(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input
        {...register('email', {
          required: 'Email is required',
          pattern: {
            value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
            message: 'Invalid email address',
          },
        })}
        placeholder="Email"
      />
      {errors.email && <span>{errors.email.message}</span>}

      <input
        {...register('password', {
          required: 'Password is required',
          minLength: {
            value: 6,
            message: 'Password must be at least 6 characters',
          },
        })}
        type="password"
        placeholder="Password"
      />
      {errors.password && <span>{errors.password.message}</span>}

      <button type="submit">Login</button>
    </form>
  );
}
```

**Benefits**:
- Minimal re-renders
- Easy validation
- Form state management
- Error handling
- Async validation support

---

## 5. Data Management & Visualization

### Recharts 2.15.2
**Purpose**: Data visualization and charts

**Usage Example**:
```typescript
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  PieChart,
  Pie,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

const data = [
  { name: 'Jan', value: 400 },
  { name: 'Feb', value: 300 },
  { name: 'Mar', value: 200 },
];

export function ProgressChart() {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="name" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Line type="monotone" dataKey="value" stroke="#8884d8" />
      </LineChart>
    </ResponsiveContainer>
  );
}
```

**Chart Types Used**:
- Line Chart - Progress over time
- Bar Chart - Comparisons
- Pie Chart - Distributions
- Area Chart - Trends

---

### Date-FNS 3.6.0
**Purpose**: Date manipulation and formatting

**Common Usage**:
```typescript
import {
  format,
  parseISO,
  addDays,
  differenceInDays,
  isToday,
  isYesterday,
} from 'date-fns';

// Format dates
format(new Date(), 'MMM dd, yyyy') // "Jan 23, 2026"

// Parse ISO strings
parseISO('2026-01-23')

// Calculate differences
differenceInDays(new Date(), pastDate)

// Check date relationships
isToday(date)
isYesterday(date)

// Add time periods
addDays(date, 7)
```

---

## 6. Utilities & Helpers

### CLSX 2.1.1
**Purpose**: Conditional CSS class names

**Usage Example**:
```typescript
import clsx from 'clsx';

interface ButtonProps {
  variant?: 'primary' | 'secondary';
  disabled?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

export function Button({ variant = 'primary', disabled, size = 'md' }: ButtonProps) {
  return (
    <button
      className={clsx(
        'px-4 py-2 rounded-lg font-semibold transition-colors',
        {
          'bg-blue-600 text-white hover:bg-blue-700': variant === 'primary',
          'bg-gray-200 text-gray-800 hover:bg-gray-300': variant === 'secondary',
          'opacity-50 cursor-not-allowed': disabled,
          'px-2 py-1 text-sm': size === 'sm',
          'px-4 py-2': size === 'md',
          'px-6 py-3 text-lg': size === 'lg',
        }
      )}
      disabled={disabled}
    >
      Click me
    </button>
  );
}
```

---

### Sonner 2.0.3
**Purpose**: Toast notifications

**Setup Example**:
```typescript
import { Toaster, toast } from 'sonner';

// In App component
export function App() {
  return (
    <>
      <Toaster />
      <YourComponents />
    </>
  );
}

// Usage
function MyComponent() {
  const handleSuccess = () => {
    toast.success('Operation completed!');
  };

  const handleError = () => {
    toast.error('Something went wrong');
  };

  const handleLoading = () => {
    const promise = new Promise(resolve => setTimeout(resolve, 2000));
    toast.promise(promise, {
      loading: 'Loading...',
      success: 'Done!',
      error: 'Error',
    });
  };

  return (
    <div>
      <button onClick={handleSuccess}>Success</button>
      <button onClick={handleError}>Error</button>
      <button onClick={handleLoading}>Promise</button>
    </div>
  );
}
```

---

### Canvas Confetti 1.9.4
**Purpose**: Celebratory confetti animations

**Usage Example**:
```typescript
import confetti from 'canvas-confetti';

export function AchievementCard() {
  const celebrate = () => {
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 },
    });
  };

  return (
    <div className="achievement-card">
      <h2>Achievement Unlocked!</h2>
      <button onClick={celebrate}>Celebrate 🎉</button>
    </div>
  );
}
```

---

## 7. Recommended Additions

### Validation Library
```bash
# Zod (recommended for type safety)
npm install zod

# Or Yup (if preferred)
npm install yup
```

**Usage with React Hook Form**:
```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const schema = z.object({
  email: z.string().email('Invalid email'),
  password: z.string().min(6, 'Password too short'),
});

type FormData = z.infer<typeof schema>;

export function LoginForm() {
  const { register, handleSubmit } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {/* Form fields */}
    </form>
  );
}
```

---

### HTTP Client Library
```bash
npm install axios
```

**Usage**:
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'https://api.example.com',
  timeout: 10000,
});

// Request interceptor
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Usage
const response = await apiClient.get('/users');
```

---

### Testing Libraries
```bash
npm install -D vitest @testing-library/react @testing-library/jest-dom
```

---

### Code Quality Tools
```bash
npm install -D eslint prettier husky lint-staged
```

---

## 8. Version Management

### Current Node.js Version
```
Required: Node.js 18.x or higher
Recommended: Node.js 20.x LTS
Check: node --version
```

### Package Manager
```bash
# Using npm (built-in with Node.js)
npm install

# Or using pnpm (faster alternative)
npm install -g pnpm
pnpm install
```

---

## 9. Dependency Updates

### Check for Updates
```bash
# Check outdated packages
npm outdated

# Check for security vulnerabilities
npm audit

# Fix vulnerabilities automatically
npm audit fix

# Interactive update
npm update
```

### Update Specific Package
```bash
npm install package-name@latest
```

### Development Workflow
1. Keep dependencies up to date
2. Test thoroughly after updates
3. Use semantic versioning
4. Use dependabot or renovate bot
5. Review changelogs before major updates

---

## Dependency Tree Overview

```
Project Root
├── UI Rendering
│   ├── React 18.3.1
│   ├── React-DOM 18.3.1
│   └── React-Router 7.12.0
├── State Management
│   └── Zustand 5.0.10
├── Component Libraries
│   ├── Radix UI (30+ components)
│   ├── Material-UI (core + icons)
│   └── Lucide React (icons)
├── Styling
│   ├── Tailwind CSS 4.1.12
│   ├── Motion 12.23.24
│   ├── Emotion (react + styled)
│   ├── Class-variance-authority
│   └── Tailwind-merge
├── Forms & Input
│   ├── React Hook Form 7.55.0
│   ├── Input-OTP
│   └── React Day Picker
├── Data & Visualization
│   ├── Recharts 2.15.2
│   ├── Date-FNS 3.6.0
│   └── React Slick
├── Utilities
│   ├── CLSX 2.1.1
│   ├── Sonner (toasts)
│   ├── Vaul (drawer)
│   ├── Canvas Confetti
│   └── Next Themes
└── Drag & Drop
    ├── React DnD
    └── React DnD HTML5 Backend
```

---

## Bundle Size Analysis

| Library | Size | Impact |
|---------|------|--------|
| React + DOM | 40KB | Core |
| Tailwind CSS | 20KB | Styling |
| Radix UI | 50KB | Components |
| Material-UI | 40KB | Components |
| Other libraries | ~150KB | Various |
| **Total (gzipped)** | **~200KB** | Acceptable |

---

## Performance Optimization Tips

1. **Code Splitting**: Use `lazy()` and `Suspense` for routes
2. **Bundle Analysis**: Analyze unused code regularly
3. **Tree Shaking**: Use ES modules for better tree-shaking
4. **Lazy Loading**: Load routes and components on demand
5. **Memoization**: Use `React.memo` for expensive components
6. **Image Optimization**: Use optimized images

---

*Generated on 23/01/2026*

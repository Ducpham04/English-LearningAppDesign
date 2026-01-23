# 📂 Detailed Directory Structure

## Project Root
```
English Learning App Design/
├── src/                           # Source code directory
├── public/                        # Static assets
├── report/                        # Project reports (this folder)
├── guidelines/                    # Design guidelines
├── package.json                   # Project dependencies
├── vite.config.ts                # Vite configuration
├── tsconfig.json                 # TypeScript configuration
├── tailwind.config.js            # Tailwind CSS configuration
├── postcss.config.mjs            # PostCSS configuration
├── index.html                    # Main HTML entry point
└── Documentation files (*.md)    # Various documentation files
```

---

## 📁 src/ Directory Structure

### src/main.tsx
Entry point for React application. Initializes the React DOM and mounts the App component.

### src/app/
**Main application container and routing**

```
src/app/
├── App.tsx                       # Main app component with routing
├── components/
│   ├── ErrorBoundary.tsx        # Error boundary for error handling
│   ├── RouterSetup.tsx          # Router configuration
│   ├── figma/                   # Figma integration components
│   ├── layout/                  # Layout components
│   │   ├── AppLayout.tsx        # Main layout wrapper
│   │   ├── Sidebar.tsx          # Desktop sidebar navigation
│   │   ├── Topbar.tsx           # Top navigation bar
│   │   ├── MobileBottomNav.tsx  # Mobile navigation
│   │   └── ProtectedRoute.tsx   # Route protection
│   └── ui/                      # Reusable UI components
│       ├── Button.tsx
│       ├── Card.tsx
│       ├── Modal.tsx
│       ├── Navbar.tsx
│       └── ... (other UI components)
```

**Responsibilities**:
- Main application routing
- Layout management
- Error handling
- Navigation setup

---

### src/features/
**Feature-specific modules organized by domain**

#### Authentication (auth/)
```
src/features/auth/
├── index.ts                     # Public exports
├── Login.tsx                    # Login page component
└── Register.tsx                 # Registration page component
```

**Responsibilities**:
- User authentication UI
- Login/Register forms
- Form validation

#### Admin Dashboard (admin/)
```
src/features/admin/
├── AdminDashboard.tsx           # Main admin dashboard
├── UserManagement.tsx           # User management page
├── ClassesManagement.tsx        # Class management page
├── ContentManagement.tsx        # Content/curriculum management
├── FaceIDManagement.tsx         # Face ID system management
├── NotificationsManagement.tsx  # Notification settings
├── PlatformAnalytics.tsx        # System-wide analytics
└── AIUsage.tsx                  # AI API usage tracking
```

**Responsibilities**:
- Admin interface
- System management
- Analytics dashboard
- User/class management

#### Teacher Features (teacher/)
```
src/features/teacher/
├── Analytics.tsx                # Teacher analytics page
├── AssignContentModal.tsx       # Modal for assigning content
├── ClassesManagement.tsx        # Manage teacher's classes
├── CreateLesson.tsx             # Create new lessons
├── LessonPlanning.tsx           # Lesson planning tools
└── ReportGeneration.tsx         # Generate student reports
```

**Responsibilities**:
- Teacher-specific features
- Class management
- Content assignment
- Progress tracking

#### Student Features (student/)
```
src/features/student/
└── lessons/                     # Student lesson components
    ├── StudentLessonList.tsx
    └── StudentLessonDetail.tsx
```

#### Gamification (gamification/)
```
src/features/gamification/
├── AchievementsScreen.tsx       # View achievements
└── GamificationBar.tsx          # Gamification status bar
```

**Responsibilities**:
- Achievement system
- Badges and rewards
- Leaderboards
- Motivation mechanics

#### Lesson (lesson/)
```
src/features/lesson/
├── LessonRouter.tsx             # Lesson routing logic
├── VideoLesson.tsx              # Video lesson player
├── AIConversation.tsx           # AI chat/conversation
├── AIResults.tsx                # AI assessment results
├── PronunciationPractice.tsx    # Pronunciation training
├── LessonItem.tsx               # Individual lesson item
└── FaceVerificationDemo.tsx     # Face verification demo
```

**Responsibilities**:
- Lesson delivery
- Interactive learning
- AI integration
- Assessment

#### Progress & Session (progress/, session/)
```
src/features/progress/
├── ProgressScreen.tsx           # Overall progress view

src/features/session/
├── SessionCard.tsx              # Session card component
├── SessionDetail.tsx            # Detailed session view
└── SessionOverview.tsx          # Session summary
```

**Responsibilities**:
- Learning progress tracking
- Session management
- Performance analytics

#### Settings (settings/)
```
src/features/settings/
├── index.ts
├── AdminSettings.tsx            # Admin settings page
├── StudentSettings.tsx          # Student settings page
└── TeacherSettings.tsx          # Teacher settings page
```

**Responsibilities**:
- User preferences
- Account settings
- Notification settings

#### Onboarding (onboarding/)
```
src/features/onboarding/
├── index.ts
├── WelcomeScreen.tsx            # Welcome screen
├── FaceEnrollment.tsx           # Face enrollment process
├── LevelSelection.tsx           # Proficiency level selection
└── LearningGoal.tsx             # Learning goal selection
```

**Responsibilities**:
- User onboarding flow
- Initial setup
- Profile configuration

#### Other Features
```
src/features/
├── examples/                    # Example components
│   └── UXEnhancementsDemo.tsx
├── test/                        # Testing related components
├── user/                        # User profile components
└── vocabulary/                  # Vocabulary features
```

---

### src/hooks/
**Custom React Hooks for reusable logic**

```
src/hooks/
└── useKeyboardShortcuts.ts     # Keyboard shortcut handler
```

**Common hooks to add**:
- `useAuth()` - Authentication state
- `useAsync()` - Async operations
- `useFetch()` - Data fetching
- `useWindowSize()` - Window size tracking
- `useLocalStorage()` - Local storage management

---

### src/services/
**API services and external integrations**

```
src/services/
├── aiSpeechService.ts           # Speech recognition API
├── faceDetectionService.ts      # Face detection API
├── gradingService.ts            # Grading logic
├── learningEngine.ts            # Learning algorithm
└── mockData.ts                  # Mock data for development
```

**Common services to implement**:
- `authService.ts` - Authentication API
- `apiClient.ts` - Centralized HTTP client
- `userService.ts` - User management API
- `lessonService.ts` - Lesson API
- `analyticsService.ts` - Analytics API

---

### src/store/
**Zustand state management**

```
src/store/
├── appStore.ts                  # Global app state
├── authStore.ts                 # Authentication state
└── gamificationStore.ts         # Gamification state
```

**State management structure**:
```typescript
// Example store structure
const store = {
  // State
  state: {},
  
  // Actions
  actions: {
    setState: () => {},
    reset: () => {},
  },
  
  // Selectors
  selectors: {
    getState: () => {},
  }
}
```

---

### src/types/
**TypeScript type definitions**

```
src/types/
├── index.ts                     # Main type exports
├── auth.ts                      # Auth-related types
├── teacher.ts                   # Teacher types
├── curriculum.ts                # Curriculum types
├── assessment.ts                # Assessment types
└── common.ts                    # Common types (base types)
```

**Common types**:
```typescript
// User types
interface User {
  id: string;
  email: string;
  name: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
}

// API response types
interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
}

// Lesson types
interface Lesson {
  id: string;
  title: string;
  description: string;
  content: string;
}
```

---

### src/styles/
**Global styles and CSS**

```
src/styles/
├── index.css                    # Main styles
├── tailwind.css                 # Tailwind CSS imports
├── theme.css                    # Theme variables
└── fonts.css                    # Font definitions
```

---

### src/utils/
**Utility functions**

```
src/utils/
├── accessibility.ts             # Accessibility utilities
└── pwa.ts                       # PWA utilities
```

**Common utilities to add**:
- `validators.ts` - Input validation
- `formatters.ts` - Data formatting
- `constants.ts` - App constants
- `helpers.ts` - General helpers

---

### src/shared/
**Shared components and utilities**

```
src/shared/
└── ui/                          # Shared UI components
```

---

## 📁 public/ Directory

```
public/
├── manifest.json                # PWA manifest
├── service-worker.js            # Service worker for offline support
└── icons/                       # App icons
    ├── favicon.ico
    ├── logo.png
    └── README.md
```

---

## 📁 Configuration Files

| File | Purpose |
|------|---------|
| `package.json` | Project dependencies & scripts |
| `vite.config.ts` | Vite build configuration |
| `tsconfig.json` | TypeScript configuration |
| `tailwind.config.js` | Tailwind CSS theme config |
| `postcss.config.mjs` | PostCSS plugins config |
| `index.html` | Main HTML file |

---

## 📁 Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Project overview |
| `API_SPECIFICATION.md` | API documentation |
| `DEVELOPER_GUIDE.md` | Developer quick reference |
| `IMPLEMENTATION_SUMMARY.md` | Implementation details |
| `SPRINT_*.md` | Sprint reports |
| `ATTRIBUTIONS.md` | Credits and attributions |

---

## 📊 Directory Statistics

| Category | Count |
|----------|-------|
| Total Directories | 30+ |
| Total Files | 100+ |
| Component Files | 50+ |
| Type Definition Files | 5 |
| Service Files | 6+ |
| Store Files | 3 |
| CSS Files | 4 |

---

## 🎯 Best Practices

### Naming Conventions
- **Folders**: kebab-case (e.g., `user-management`)
- **Components**: PascalCase (e.g., `UserManagement.tsx`)
- **Utilities**: camelCase (e.g., `userValidator.ts`)
- **Types**: PascalCase with 'Type' suffix (e.g., `UserType.ts`)

### File Organization
- Group related features in folders
- Keep components small and focused
- Separate logic into services/hooks
- Centralize type definitions
- Use barrel exports (index.ts) for clean imports

### Import Conventions
```typescript
// Use path aliases
import { UserCard } from '@/app/components/ui';
import { useAuth } from '@/hooks';
import { userService } from '@/services';
import type { User } from '@/types';

// Avoid long relative paths
// ❌ import { UserCard } from '../../../components/ui';
// ✅ import { UserCard } from '@/app/components/ui';
```

---

*Generated on 23/01/2026*

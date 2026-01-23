# 🛠️ Technology Stack & Dependencies

## Frontend Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **React** | 18.3.1 | UI library |
| **TypeScript** | Latest | Type safety |
| **Vite** | 6.3.5 | Build tool & dev server |
| **Tailwind CSS** | 4.1.12 | Styling framework |
| **React Router** | 7.12.0 | Client-side routing |
| **Zustand** | 5.0.10 | State management |

---

## Package Dependencies

### UI Component Libraries

#### Material-UI (MUI)
```json
{
  "@mui/material": "7.3.5",
  "@mui/icons-material": "7.3.5"
}
```
**Purpose**: Pre-built professional UI components
**Key Components**: Buttons, Cards, Dialogs, Icons

#### Radix UI
```json
{
  "@radix-ui/react-*": "Latest"
}
```
**Components Included**:
- Dialog, Alert Dialog
- Dropdown Menu, Context Menu
- Tabs, Accordion
- Checkbox, Radio Group
- Select, Popover
- Tooltip, Hover Card
- Slider, Progress
- Switch, Toggle

**Purpose**: Accessible, unstyled component primitives

---

### Styling & Animation

#### Motion
```json
{
  "motion": "12.23.24"
}
```
**Purpose**: Smooth animations and transitions
**Key Features**:
- Keyframe animations
- Gesture animations
- Scroll animations

#### Tailwind CSS Utilities
```json
{
  "@tailwindcss/vite": "4.1.12",
  "tailwind-merge": "3.2.0",
  "class-variance-authority": "0.7.1",
  "tw-animate-css": "1.3.8"
}
```

**Purpose**: Utility-first CSS styling
**Features**:
- Responsive design
- Dark mode support
- Custom theme configuration
- Animation utilities

---

### Form & Input

#### React Hook Form
```json
{
  "react-hook-form": "7.55.0"
}
```
**Purpose**: Flexible form management
**Features**:
- Form validation
- Error handling
- Performance optimization
- Minimal re-renders

#### Validation
- **Built-in**: HTML5 validation
- **Custom**: Form validators
- **Schema Validation**: [To integrate - suggest Zod or Yup]

---

### Icons & Graphics

#### Lucide React
```json
{
  "lucide-react": "0.487.0"
}
```
**Purpose**: SVG icon library
**Features**:
- 1000+ icons
- Tree-shakeable
- Consistent design

#### Canvas Confetti
```json
{
  "canvas-confetti": "1.9.4"
}
```
**Purpose**: Celebratory confetti animations
**Use Case**: Achievement celebrations, gamification rewards

---

### Data & Time

#### Date-FNS
```json
{
  "date-fns": "3.6.0"
}
```
**Purpose**: Date manipulation and formatting
**Key Functions**:
- Format dates
- Parse dates
- Calculate differences
- Localization

#### Recharts
```json
{
  "recharts": "2.15.2"
}
```
**Purpose**: Data visualization
**Charts**:
- Line, Bar, Area charts
- Pie, Radar charts
- Composed charts
- Responsive design

---

### Drag & Drop

#### React DnD
```json
{
  "react-dnd": "16.0.1",
  "react-dnd-html5-backend": "16.0.1"
}
```
**Purpose**: Drag and drop functionality
**Use Cases**:
- Lesson reordering
- Content assignment
- Class organization

#### React Beautiful DnD
**Alternative for**: Drag and drop with smooth animations

---

### Carousel & Sliders

#### React Slick
```json
{
  "react-slick": "0.31.0"
}
```
**Purpose**: Carousel component
**Features**:
- Responsive design
- Touch support
- Autoplay
- Navigation controls

#### Embla Carousel
```json
{
  "embla-carousel-react": "8.6.0"
}
```
**Purpose**: Lightweight carousel library
**Benefits**: Better performance, smaller bundle size

---

### Utilities & Helpers

#### CLSX
```json
{
  "clsx": "2.1.1"
}
```
**Purpose**: Conditional CSS class names
**Example**:
```typescript
clsx('btn', {
  'btn-primary': isPrimary,
  'btn-disabled': isDisabled
})
```

#### Sonner
```json
{
  "sonner": "2.0.3"
}
```
**Purpose**: Toast notifications
**Features**:
- Toast types (success, error, warning)
- Auto-dismiss
- Custom styling

#### Vaul
```json
{
  "vaul": "1.1.2"
}
```
**Purpose**: Sheet/drawer component
**Use Cases**: Mobile menus, side panels

---

### Theme & Appearance

#### Next Themes
```json
{
  "next-themes": "0.4.6"
}
```
**Purpose**: Theme management (light/dark mode)
**Features**:
- Theme switching
- Persistence
- No flash on load

---

### Emotion (CSS-in-JS)
```json
{
  "@emotion/react": "11.14.0",
  "@emotion/styled": "11.14.1"
}
```
**Purpose**: CSS-in-JS styling
**Features**:
- Dynamic styling
- Scoped styles
- Theme support

---

### Advanced Components

#### Input OTP
```json
{
  "input-otp": "1.4.2"
}
```
**Purpose**: One-time password input
**Use Cases**: 2FA, verification codes

#### React Day Picker
```json
{
  "react-day-picker": "8.10.1"
}
```
**Purpose**: Calendar/date picker component
**Features**:
- Date range selection
- Disabled dates
- Localization

#### React Resizable Panels
```json
{
  "react-resizable-panels": "2.1.7"
}
```
**Purpose**: Resizable panel layouts
**Use Cases**: Admin dashboards, IDE-like layouts

#### React Responsive Masonry
```json
{
  "react-responsive-masonry": "2.7.1"
}
```
**Purpose**: Masonry/grid layout
**Use Cases**: Gallery views, content feeds

---

## Development Dependencies

```json
{
  "@vitejs/plugin-react": "4.7.0",
  "tailwindcss": "4.1.12",
  "vite": "6.3.5"
}
```

### Build & Dev Tools
- **Vite**: Fast build tool and dev server
- **Tailwind CSS**: Utility-first CSS framework
- **React Plugin**: JSX support in Vite

---

## Browser Support

- Chrome: Latest 2 versions
- Firefox: Latest 2 versions
- Safari: Latest 2 versions
- Edge: Latest 2 versions

---

## External APIs & Services (To Integrate)

### Speech Recognition
- **Google Cloud Speech-to-Text** OR
- **Azure Speech Services** OR
- **Web Speech API** (browser native)

### Face Detection & Recognition
- **Face-api.js** OR
- **Google Cloud Vision API** OR
- **Azure Face API** OR
- **TensorFlow.js** (on-device)

### AI Chatbot
- **OpenAI GPT API** OR
- **Google Bard API** OR
- **Azure OpenAI** OR
- **Hugging Face Inference API**

### Analytics
- **Google Analytics 4** OR
- **Mixpanel** OR
- **Amplitude**

### Authentication
- **Firebase Authentication** OR
- **Auth0** OR
- **Okta** OR
- **Custom JWT-based auth**

---

## Database (Backend - To be implemented)

### Recommended
- **PostgreSQL**: Relational data (users, lessons, progress)
- **Redis**: Caching and real-time features
- **MongoDB**: Optional for flexible content storage

---

## Backend Stack (To be implemented)

### Recommended Options

#### Option 1: Node.js/Express (JavaScript)
```
Node.js 18+
├── Express.js - Web framework
├── TypeScript - Type safety
├── Prisma - ORM
├── PostgreSQL - Database
└── Redis - Cache
```

#### Option 2: Python/FastAPI
```
Python 3.10+
├── FastAPI - Web framework
├── SQLAlchemy - ORM
├── PostgreSQL - Database
└── Redis - Cache
```

#### Option 3: .NET/C#
```
.NET 7+
├── ASP.NET Core - Web framework
├── Entity Framework Core - ORM
├── SQL Server - Database
└── Redis - Cache
```

---

## DevOps & Deployment

### Frontend Hosting
- **Vercel** (Recommended)
- **Netlify**
- **GitHub Pages**
- **AWS S3 + CloudFront**

### Backend Hosting
- **AWS EC2/ECS**
- **Google Cloud Run**
- **Heroku**
- **DigitalOcean**

### Database Hosting
- **AWS RDS**
- **Google Cloud SQL**
- **Heroku Postgres**
- **Railway**

### CI/CD
- **GitHub Actions**
- **GitLab CI**
- **Jenkins**
- **CircleCI**

---

## Monitoring & Logging

### Error Tracking
- **Sentry**
- **Rollbar**
- **BugSnag**

### Performance Monitoring
- **New Relic**
- **Datadog**
- **CloudWatch**

### Logging
- **Winston** (Node.js)
- **Structlog** (Python)
- **Serilog** (.NET)

---

## Version Management

### Node.js
```
Required: Node.js 18.x or higher
Recommended: Node.js 20.x LTS
```

### Package Manager
```
npm (included with Node.js)
OR
pnpm (faster, more reliable)
```

---

## Performance Optimization

### Current Implementations
- ✅ Code splitting with Vite
- ✅ Lazy loading routes
- ✅ Component memoization
- ⏳ Image optimization (to add)
- ⏳ Bundle analysis (to add)

### Recommended Additions
- Image optimization library (sharp, imagemin)
- Bundle analyzer (webpack-bundle-analyzer)
- Performance monitoring tools
- Lazy loading for images (next/image or similar)

---

## Testing Stack (To be implemented)

### Unit Testing
- **Vitest** or **Jest** - Test runner
- **React Testing Library** - Component testing
- **@testing-library/user-event** - User interaction testing

### E2E Testing
- **Cypress** or **Playwright** - E2E testing
- **Selenium** - Alternative E2E tool

### Performance Testing
- **Lighthouse** - Performance audits
- **WebPageTest** - Performance analysis

---

## Recommended Additions

### Package Additions
```json
{
  "zod": "Latest",              // Schema validation
  "axios": "Latest",            // HTTP client
  "lodash-es": "Latest",        // Utility functions
  "uuid": "Latest",             // ID generation
  "js-cookie": "Latest",        // Cookie management
  "dotenv": "Latest"            // Environment variables
}
```

### Development Tools
```json
{
  "eslint": "Latest",           // Linting
  "prettier": "Latest",         // Code formatting
  "husky": "Latest",            // Git hooks
  "lint-staged": "Latest",      // Pre-commit checks
  "vitest": "Latest",           // Unit testing
  "@testing-library/react": "Latest"
}
```

---

## Dependency Management

### Security Updates
- Run `npm audit` regularly
- Use Dependabot for automatic updates
- Review and test updates before merging

### Performance
- Keep dependencies up to date
- Remove unused packages
- Monitor bundle size
- Use tree-shaking compatible packages

---

## PWA Features

### Implemented
- ✅ Service Worker support
- ✅ Manifest file
- ✅ Offline capability

### To Add
- App shell caching strategy
- Push notifications
- Background sync
- Install prompts

---

*Generated on 23/01/2026*

# Session Page UI/UX Redesign – EdTech Learning Platform

**Version:** 1.0  
**Date:** March 2026  
**Focus:** Full-screen layout, modern navigation, session cards, learning motivation, gamification.

---

## 1. Design Goals

- **Full-screen utilization** – No cramped `max-w-3xl`; use full width with a consistent max content width (e.g. 1400px) and generous padding.
- **Modern, minimal navigation** – Clear hierarchy, obvious “you are here,” quick access to learning path.
- **Session as cards** – Replace plain lists with visual cards: image, title, description, duration, difficulty, primary CTA (Start / Continue).
- **Learning motivation** – Progress, completed/locked states, and a visible learning path to encourage exploration.
- **Gamification** – Progress bars, streaks, badges, achievements visible in-context (e.g. top bar or hero on session page).
- **Visual hierarchy** – Important actions and “next step” stand out; secondary info is visible but not competing.

---

## 2. Full-Screen Layout

### 2.1 Structure

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Sidebar (collapsible, ~72px icon-only or ~240px with labels)                │
├──────────────┬──────────────────────────────────────────────────────────────┤
│              │  Topbar: [Breadcrumb / Page title]  [XP | Streak] [Avatar]    │
│              ├──────────────────────────────────────────────────────────────┤
│              │                                                               │
│              │  Main content: full width, max-w-[1400px] mx-auto px-6 py-8   │
│              │  – Hero / gamification strip (on session page)               │
│              │  – Filters / search (optional)                                │
│              │  – Session cards grid (e.g. 2–3 columns on desktop)         │
│              │  – Learning path connector (optional)                         │
│              │                                                               │
└──────────────┴──────────────────────────────────────────────────────────────┘
```

- **Main content:** `w-full`, `max-w-[1400px]`, `mx-auto`, `px-6` (or `px-4` mobile), `py-6`–`py-10`.
- **No** fixed small `max-w-3xl` on session list; let the grid breathe.

### 2.2 Responsive

- **Desktop (≥1024px):** Sidebar visible, 3-column card grid.
- **Tablet (768–1023px):** Sidebar visible or collapsible, 2-column grid.
- **Mobile (&lt;768px):** Bottom nav, full-width cards (1 column), top bar with compact streak/XP.

---

## 3. Modern Navigation

### 3.1 Sidebar (Desktop)

- **Width:** Default ~240px; collapsed ~72px (icons only).
- **Style:** Light border, subtle background (`bg-white` / `dark:bg-gray-900`), no heavy shadows.
- **Active state:** Left border (3–4px) in brand color (e.g. violet/emerald) + light background tint, not only filled button.
- **Items:** Icon + label; optional badge (e.g. “New”) for one item.
- **Hierarchy:** Logo at top; primary nav (Dashboard, Chủ đề/Sessions, Tests, Progress, Achievements, Profile); Settings at bottom.
- **Student “Chủ đề”** = link to `/sessions`; label as “Learning” or “Sessions” for clarity.

### 3.2 Topbar

- **Left:** Breadcrumb when in session flow (e.g. Chủ đề → Topic name → Session name) or page title.
- **Center/Right:** For students on session page: compact **gamification strip** (Level, XP progress, Streak).
- **Right:** Notifications, Avatar + dropdown (Profile, Settings, Logout).

### 3.3 Mobile Bottom Nav

- 5 items: Home, Sessions (Chủ đề), Tests, Achievements (Rewards), Profile.
- Active: brand color + optional filled icon or underline.
- Labels short and clear.

### 3.4 “You are here”

- Breadcrumb in topbar for: Home > Sessions > [Topic name] (and optionally > [Session name]).
- When a topic is selected, show a **learning path** strip or stepper (e.g. Topic → Session 1 → Session 2 …) so users see where they are in the journey.

---

## 4. Session Card Design

Each **session** (and optionally each **topic**) should be a card, not a row of text.

### 4.1 Card Contents

| Element           | Required | Notes                                                                 |
|------------------|----------|-----------------------------------------------------------------------|
| Image/Illustration | Yes      | Placeholder gradient + icon if no thumbnail; otherwise topic/session image. |
| Session title    | Yes      | One line, font-semibold, truncate.                                    |
| Short description| Yes      | 1–2 lines, muted color, line-clamp-2.                                |
| Duration         | Yes      | e.g. “~15 phút” with Clock icon.                                     |
| Difficulty       | Yes      | Badge: Cơ bản / Trung cấp / Nâng cao (color-coded).                   |
| Progress         | Optional | Thin progress bar (e.g. 2/5 lessons).                               |
| Status           | Yes      | Completed (checkmark) / Available (Start) / Locked (lock icon).      |
| Primary CTA      | Yes      | Button: “Bắt đầu” / “Tiếp tục” / disabled + “Hoàn thành” for locked.  |

### 4.2 Card Layout (suggestion)

- **Aspect ratio:** Image area ~16/9 or 4/3 (e.g. h-36 or h-40).
- **Hover:** Slight scale (1.02), shadow lift, border color change.
- **Spacing:** Padding p-4 or p-5; gap between elements consistent (e.g. 3–4).

### 4.3 Visual States

- **Available:** Full color, CTA “Bắt đầu” or “Tiếp tục”.
- **Completed:** Green accent (border or badge), checkmark, optional “Xem lại” secondary CTA.
- **Locked:** Reduced opacity (e.g. 0.7), lock icon, disabled button, tooltip “Hoàn thành session trước”.

---

## 5. Learning Motivation

### 5.1 Progress Bars

- **Topic level:** e.g. “3/8 sessions completed” with a thin progress bar in the topic hero.
- **Session level:** “2/5 lessons” on each session card and on session detail page.

### 5.2 Completed vs Locked

- **Completed:** Green checkmark, “Đã hoàn thành,” optional XP or badge.
- **Locked:** Grayed card, lock icon, no navigation until previous session is done (or show “coming soon” for demo).

### 5.3 Learning Path

- When viewing sessions under a topic, show a **path** (e.g. vertical timeline or horizontal stepper):
  - Session 1 (done) → Session 2 (current) → Session 3 (locked).
- Use connector lines or “path” illustration (dots + lines) between cards.

### 5.4 “What to do next”

- One **recommended** card (e.g. “Tiếp tục học” or “Bắt đầu session tiếp theo”) with stronger visual emphasis (border, background tint, or badge “Đề xuất”).

---

## 6. Gamification Elements

### 6.1 In Context (Session Page)

- **Top bar strip (students):** Level, XP progress bar to next level, Streak (flame + number).
- **Optional in hero:** “Bạn đã hoàn thành 3 session trong chủ đề này” + small badge or confetti on new completion.

### 6.2 Progress Tracking

- Dashboard and session page both show:
  - Sessions completed per topic.
  - Lessons completed per session.
  - Overall XP and level.

### 6.3 Streaks

- Visible in top bar: “7 ngày” + flame icon; color intensity by streak length.
- Optional: “Giữ chuỗi: hoàn thành 1 bài hôm nay.”

### 6.4 Badges & Achievements

- Link from top bar or session hero to “Achievements” page.
- Optional: small badge icons on completed session cards (“First session”, “Streak 7”, etc.).

---

## 7. Modern EdTech Aesthetic

### 7.1 Color Palette (suggestion)

- **Primary (actions, links, active):** Violet/Purple (e.g. `violet-500`, `violet-600`).
- **Success / completed:** Emerald/Green (`emerald-500`, `emerald-600`).
- **Warning / difficulty intermediate:** Amber (`amber-500`).
- **Danger / difficulty advanced:** Rose (`rose-500`).
- **Neutrals:** Gray scale; background `gray-50` / `gray-100` (light), `gray-900` (dark).
- **Surfaces:** White cards, subtle border `gray-200` / `dark:gray-700`.

### 7.2 Typography

- **Headings:** Bold, clear hierarchy (text-2xl/3xl for page title, text-lg/xl for section).
- **Body:** text-sm/base, readable line-height (1.5–1.6).
- **Font:** System stack or one primary (e.g. Inter, Plus Jakarta Sans) for a modern EdTech look.

### 7.3 Spacing

- **Base unit:** 4px; use 4, 6, 8, 10, 12, 16, 24, 32.
- **Section spacing:** py-8 or py-10 between major sections.
- **Card internal:** p-4 or p-5; gap-3 / gap-4 between elements.

### 7.4 Card Design

- Rounded corners: `rounded-2xl`.
- Border: 1px, subtle.
- Shadow: default `shadow-sm`, hover `shadow-lg` or `shadow-xl`.
- No heavy gradients on every card; reserve gradient for hero or one “featured” card.

### 7.5 Hover / Micro-interactions

- Cards: `transition-all duration-200`, hover scale 1.02, shadow lift.
- Buttons: slight scale on press (0.98).
- Progress bars: smooth transition when value changes.

---

## 8. Layout Structure & Component Hierarchy

### 8.1 Page: Session Overview (Topic list + Session list)

```
SessionOverviewPage
├── LayoutContainer (full width, max-w-[1400px], padding)
│   ├── Breadcrumb (optional: Home > Chủ đề [> Topic name])
│   ├── PageHeader (title + subtitle)
│   ├── GamificationStrip (compact: Level, XP, Streak) – or in Topbar
│   ├── SearchOrFilters (optional)
│   └── Content
│       ├── If no topic selected: TopicCardGrid
│       │   └── TopicCard[] (image, title, description, session count, duration, CTA)
│       └── If topic selected: LearningPath + SessionCardGrid
│           ├── TopicHero (title, description, progress, back button)
│           ├── SessionCard[] (image, title, description, duration, difficulty, progress, status, CTA)
│           └── Optional: PathConnector (visual line/dots between sessions)
```

### 8.2 Page: Session Detail

```
SessionDetailPage
├── LayoutContainer
│   ├── BackButton (to sessions list or topic)
│   ├── SessionHero (title, topic name, stats, progress bar)
│   ├── LessonList (cards or list with type icon, title, duration, locked/completed)
│   └── Optional: GamificationStrip or “Next up” CTA
```

### 8.3 Reusable Components

- **SessionCard** – Image, title, description, duration, difficulty, progress, status, CTA.
- **TopicCard** – Same idea for topic level.
- **GamificationStrip** – Level, XP bar, Streak (compact for top bar).
- **ProgressBar** – Reusable (topic/session/lesson).
- **DifficultyBadge** – Cơ bản / Trung cấp / Nâng cao.
- **LearningPathStepper** – Dots + labels for current topic’s sessions.

---

## 9. UI Patterns (Duolingo, Coursera, Notion)

- **Duolingo:** Strong gamification (hearts, streaks, XP), path with nodes, clear “next lesson,” playful but simple.
- **Coursera:** Card-based course/session list, progress on cards, clean typography, “Resume” vs “Start”.
- **Notion:** Sidebar with icons + labels, breadcrumb, clean hierarchy, minimal color until interaction.

**Takeaways:** Use cards for sessions, show progress on cards, one clear primary CTA (Start/Continue), breadcrumb + learning path, and a compact gamification strip so users always see progress and streak.

---

## 10. React + Tailwind Component Ideas

### 10.1 SessionCard (conceptual)

```tsx
<SessionCard
  title={session.title}
  description={session.description}
  durationMinutes={estimatedTime}
  difficulty={difficulty}
  progress={{ completed: 2, total: 5 }}
  status="available" | "completed" | "locked"
  thumbnailUrl={session.thumbnailUrl}
  onStart={() => navigate(`/sessions/${session.id}`)}
/>
```

- Outer: `rounded-2xl border bg-white dark:bg-gray-800 overflow-hidden shadow-sm hover:shadow-xl transition-all hover:-translate-y-0.5`.
- Image: `h-36 bg-gradient-to-br from-violet-500 to-purple-600` with optional `img` or icon overlay.
- Body: `p-4` or `p-5`, flex column, gap-3.
- CTA: Full-width or right-aligned Button (Bắt đầu / Tiếp tục / disabled).

### 10.2 GamificationStrip (compact for Topbar)

- Horizontal: `[Level pill] [XP progress bar] [Streak pill]`.
- Level: rounded-full bg, number.
- XP: Progress component, optional “X XP / Y XP”.
- Streak: Flame icon + number, orange when > 0.

### 10.3 LearningPathStepper

- Horizontal: circles or dots for each session; lines between; current = filled/brand color, completed = checkmark, locked = lock or gray.

### 10.4 TopicHero

- Gradient background, title, description, “X/Y sessions” + progress bar, back button, optional breadcrumb.

---

## 11. Summary Checklist

- [x] Full-width layout with max-w-[1400px] and consistent padding.
- [x] Modern sidebar: clear active state, optional collapse.
- [x] Session (and topic) as cards: image, title, description, duration, difficulty, progress, status, CTA.
- [x] Learning motivation: progress bars, completed/locked, learning path, “next” suggestion.
- [x] Gamification: strip in topbar (Level, XP, Streak); optional badges on cards.
- [x] Visual hierarchy: hero, then cards; primary CTA prominent.
- [x] EdTech aesthetic: violet/emerald palette, rounded cards, hover states, clear typography.
- [x] Component breakdown and React + Tailwind ideas documented.

This document can be used as the single source of truth for the Session page redesign and future iterations.

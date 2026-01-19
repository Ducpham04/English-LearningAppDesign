# 🚀 Quick Start Guide - UX Enhancements

## Table of Contents
1. [Loading States](#loading-states)
2. [Empty States](#empty-states)
3. [Error Handling](#error-handling)
4. [Animations](#animations)
5. [Keyboard Shortcuts](#keyboard-shortcuts)
6. [Tutorial Tooltips](#tutorial-tooltips)
7. [PWA Features](#pwa-features)
8. [Accessibility](#accessibility)

---

## Loading States

### Basic Usage

```tsx
import { DashboardSkeleton, SectionLoading } from '@/shared/ui/LoadingStates';

function MyComponent() {
  const { data, isLoading } = useQuery('myData');
  
  if (isLoading) {
    return <DashboardSkeleton />;
  }
  
  return <div>{data}</div>;
}
```

### Available Components
- `PageLoading` - Full page spinner
- `SectionLoading` - Section spinner with message
- `DashboardSkeleton` - Dashboard cards skeleton
- `SessionCardSkeleton` - Session card placeholder
- `LessonCardSkeleton` - Lesson card placeholder
- `TableSkeleton` - Table skeleton with rows/cols
- `ChartSkeleton` - Chart placeholder
- `ListSkeleton` - List items skeleton
- `InlineLoading` - Small inline spinner

---

## Empty States

### Basic Usage

```tsx
import { NoSessionsEmpty, NoDataEmpty } from '@/shared/ui/EmptyStates';

function SessionList({ sessions }) {
  if (sessions.length === 0) {
    return (
      <NoSessionsEmpty 
        onCreateSession={() => navigate('/sessions/new')} 
      />
    );
  }
  
  return <div>Sessions list</div>;
}
```

### Custom Empty State

```tsx
import { EmptyState } from '@/shared/ui/EmptyStates';
import { BookOpen } from 'lucide-react';

<EmptyState
  icon={BookOpen}
  title="No Content Yet"
  description="Start by creating your first item"
  action={{
    label: "Create New",
    onClick: handleCreate
  }}
  secondaryAction={{
    label: "Learn More",
    onClick: handleLearnMore
  }}
/>
```

---

## Error Handling

### Toast Notifications

```tsx
import { showErrorToast, showSuccessToast } from '@/shared/ui/ErrorStates';

// Show error
showErrorToast('Failed to save data');

// Show success
showSuccessToast('Data saved successfully!');

// Loading toast
const toastId = showLoadingToast('Saving...');
// Later: toast.dismiss(toastId);
```

### Full Error Pages

```tsx
import { NetworkError, ServerError } from '@/shared/ui/ErrorStates';

function DataFetcher() {
  const { error } = useQuery('data');
  
  if (error?.type === 'network') {
    return <NetworkError onRetry={refetch} />;
  }
  
  if (error?.type === 'server') {
    return <ServerError onRetry={refetch} />;
  }
  
  return <Content />;
}
```

### Inline Errors

```tsx
import { InlineError } from '@/shared/ui/ErrorStates';

<InlineError
  title="Validation Error"
  message="Please check your input"
  onRetry={handleRetry}
  onDismiss={() => setError(null)}
/>
```

---

## Animations

### Page Transitions

```tsx
import { SlideUp, FadeIn } from '@/shared/ui/AnimationWrappers';

function MyPage() {
  return (
    <SlideUp>
      <h1>My Page</h1>
      <FadeIn delay={0.2}>
        <p>Content appears after heading</p>
      </FadeIn>
    </SlideUp>
  );
}
```

### List Animations

```tsx
import { StaggerChildren, StaggerItem } from '@/shared/ui/AnimationWrappers';

<StaggerChildren staggerDelay={0.1}>
  {items.map(item => (
    <StaggerItem key={item.id}>
      <Card>{item.name}</Card>
    </StaggerItem>
  ))}
</StaggerChildren>
```

### Interactive Animations

```tsx
import { HoverScale, Shake } from '@/shared/ui/AnimationWrappers';

// Hover effect
<HoverScale scale={1.05}>
  <Button>Hover me!</Button>
</HoverScale>

// Error shake
const [hasError, setHasError] = useState(false);

<Shake trigger={hasError}>
  <Input />
</Shake>
```

### Success Animations

```tsx
import { SuccessCheckmark, BounceIn } from '@/shared/ui/AnimationWrappers';

// Checkmark
<SuccessCheckmark className="size-16" />

// Bounce entrance
<BounceIn>
  <Card>Success message!</Card>
</BounceIn>
```

---

## Keyboard Shortcuts

### Global Shortcuts (Already Active)
- `Ctrl + K` - Search
- `/` - Focus search
- `Shift + ?` - Show shortcuts help
- `Esc` - Close modal

### Navigation Shortcuts
- `G + H` - Go to Home
- `G + S` - Go to Sessions
- `G + P` - Go to Profile
- `G + V` - Go to Vocabulary

### Custom Shortcuts

```tsx
import { useKeyboardShortcut } from '@/hooks/useKeyboardShortcuts';

function MyComponent() {
  useKeyboardShortcut('n', handleNext);
  useKeyboardShortcut('p', handlePrevious);
  useKeyboardShortcut('s', handleSave, { ctrl: true });
  
  return <Content />;
}
```

### Lesson Shortcuts

```tsx
import { useLessonShortcuts } from '@/hooks/useKeyboardShortcuts';

function Lesson() {
  useLessonShortcuts({
    onNext: () => nextSlide(),
    onPrevious: () => prevSlide(),
    onSubmit: () => submitAnswer(),
    onPlayPause: () => toggleAudio()
  });
  
  return <LessonContent />;
}
```

### Show Shortcuts Help

```tsx
import { KeyboardShortcutsHelp } from '@/hooks/useKeyboardShortcuts';

<KeyboardShortcutsHelp role="student" />
```

---

## Tutorial Tooltips

### Basic Tutorial

```tsx
import { TutorialTooltip } from '@/shared/ui/TutorialTooltips';

const steps = [
  {
    target: '#sessions-button',
    title: 'Sessions',
    content: 'View all your learning sessions here',
    placement: 'bottom',
    highlight: true
  },
  {
    target: '#profile-button',
    title: 'Your Profile',
    content: 'Check your progress and achievements',
    placement: 'left'
  }
];

function Dashboard() {
  return (
    <>
      <TutorialTooltip
        steps={steps}
        onComplete={() => console.log('Done!')}
        onSkip={() => console.log('Skipped')}
        storageKey="dashboard-tour"
      />
      <DashboardContent />
    </>
  );
}
```

### Feature Highlight

```tsx
import { FeatureHighlight } from '@/shared/ui/TutorialTooltips';

const [showFeature, setShowFeature] = useState(true);

<FeatureHighlight
  title="New Feature!"
  description="You can now share your achievements"
  show={showFeature}
  onDismiss={() => setShowFeature(false)}
/>
```

### Inline Tips

```tsx
import { InlineTip } from '@/shared/ui/TutorialTooltips';

<InlineTip tip="Click here to get started">
  <Button>Start</Button>
</InlineTip>
```

---

## PWA Features

### Install Banner

The install banner appears automatically after 30 seconds for new users.

```tsx
import { InstallPWABanner } from '@/shared/ui/InstallPWABanner';

// Add to your App or Layout
<InstallPWABanner />
```

### PWA Utilities

```tsx
import {
  requestNotificationPermission,
  showNotification,
  shareContent,
  requestWakeLock,
  isAppInstalled,
  isOnline
} from '@/utils/pwa';

// Notifications
if (await requestNotificationPermission()) {
  showNotification('Lesson Complete!', {
    body: 'You earned 50 XP!',
    icon: '/icons/icon-192x192.png'
  });
}

// Share
await shareContent({
  title: 'Check this out!',
  text: 'I completed 100 lessons!',
  url: window.location.href
});

// Wake Lock (keep screen on)
await requestWakeLock();

// Check if installed
if (isAppInstalled()) {
  console.log('Running as installed app');
}

// Check connection
if (!isOnline()) {
  showErrorToast('No internet connection');
}
```

---

## Accessibility

All features are automatically initialized. Manual usage:

### Screen Reader Announcements

```tsx
import { announceToScreenReader } from '@/utils/accessibility';

// Polite announcement
announceToScreenReader('Page loaded');

// Urgent announcement
announceToScreenReader('Error occurred!', 'assertive');
```

### Focus Management

```tsx
import { trapFocus, scrollToError } from '@/utils/accessibility';

// Trap focus in modal
useEffect(() => {
  if (isOpen) {
    const cleanup = trapFocus(modalRef.current);
    return cleanup;
  }
}, [isOpen]);

// Scroll to error
if (hasError) {
  scrollToError(errorElementRef.current);
}
```

### ARIA Labels

```tsx
import { setAriaLabel, setAriaExpanded } from '@/utils/accessibility';

const buttonRef = useRef(null);

useEffect(() => {
  setAriaLabel(buttonRef.current, 'Close dialog');
  setAriaExpanded(buttonRef.current, isOpen);
}, [isOpen]);
```

---

## Common Patterns

### Complete Page Example

```tsx
import { useState, useEffect } from 'react';
import { SlideUp, StaggerChildren, StaggerItem } from '@/shared/ui/AnimationWrappers';
import { DashboardSkeleton } from '@/shared/ui/LoadingStates';
import { NoDataEmpty } from '@/shared/ui/EmptyStates';
import { NetworkError } from '@/shared/ui/ErrorStates';
import { useKeyboardShortcut } from '@/hooks/useKeyboardShortcuts';
import { announceToScreenReader } from '@/utils/accessibility';

function MyPage() {
  const { data, isLoading, error, refetch } = useQuery('data');
  
  // Keyboard shortcut
  useKeyboardShortcut('r', refetch, { ctrl: true });
  
  // Announce page load
  useEffect(() => {
    announceToScreenReader('Dashboard loaded');
  }, []);
  
  // Loading state
  if (isLoading) return <DashboardSkeleton />;
  
  // Error state
  if (error) return <NetworkError onRetry={refetch} />;
  
  // Empty state
  if (!data?.length) return <NoDataEmpty />;
  
  // Success state with animations
  return (
    <SlideUp>
      <h1>My Page</h1>
      <StaggerChildren>
        {data.map(item => (
          <StaggerItem key={item.id}>
            <Card>{item.name}</Card>
          </StaggerItem>
        ))}
      </StaggerChildren>
    </SlideUp>
  );
}
```

### Form with Validation

```tsx
import { Shake } from '@/shared/ui/AnimationWrappers';
import { InlineError, showSuccessToast } from '@/shared/ui/ErrorStates';
import { announceToScreenReader } from '@/utils/accessibility';

function MyForm() {
  const [error, setError] = useState(null);
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await saveData();
      showSuccessToast('Saved!');
      announceToScreenReader('Form submitted successfully');
    } catch (err) {
      setError(err.message);
      announceToScreenReader('Error: ' + err.message, 'assertive');
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <Shake trigger={!!error}>
        {error && <InlineError message={error} />}
      </Shake>
      <Input aria-label="Email address" />
      <Button type="submit">Save</Button>
    </form>
  );
}
```

---

## Testing

### Test Loading States
```tsx
// Toggle loading manually
const [isLoading, setIsLoading] = useState(false);
<Button onClick={() => setIsLoading(!isLoading)}>Toggle Loading</Button>
```

### Test Empty States
```tsx
// Use empty array
const items = [];
if (items.length === 0) return <NoDataEmpty />;
```

### Test Keyboard Shortcuts
```tsx
// Add console log to verify
useKeyboardShortcut('t', () => console.log('Shortcut works!'));
```

### Test PWA
1. Build for production: `npm run build`
2. Serve: `npx serve dist`
3. Open DevTools > Application > Manifest
4. Test "Add to Home Screen"

---

## Tips & Best Practices

✅ **DO:**
- Use loading skeletons for better perceived performance
- Provide clear error messages with retry options
- Add keyboard shortcuts for common actions
- Wrap page content with animations
- Test with reduced motion enabled
- Verify ARIA labels on interactive elements

❌ **DON'T:**
- Use generic loading spinners everywhere
- Show empty states without helpful actions
- Forget to handle offline scenarios
- Overuse animations (respect reduced motion)
- Ignore keyboard navigation
- Skip accessibility testing

---

## Demo Page

Visit `/examples/ux-demo` to see all features in action!

```tsx
import { UXEnhancementsDemo } from '@/features/examples/UXEnhancementsDemo';

// Add route
<Route path="/examples/ux-demo" element={<UXEnhancementsDemo />} />
```

---

## Resources

- **Documentation**: `/SPRINT_3_UX_ENHANCEMENTS.md`
- **Components**: `/src/shared/ui/`
- **Hooks**: `/src/hooks/`
- **Utilities**: `/src/utils/`
- **PWA**: `/public/manifest.json`, `/public/service-worker.js`

---

**Need Help?**
- Check the demo page for live examples
- Review the Sprint 3 documentation
- Test in different browsers and devices
- Use Chrome DevTools for PWA testing

Happy coding! 🚀

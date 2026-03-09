import { Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { Button } from '@/app/components/ui/button';
import { ScrollArea } from '@/app/components/ui/scroll-area';
import { Separator } from '@/app/components/ui/separator';
import {
  Home,
  BookOpen,
  Book,
  User,
  GraduationCap,
  Users,
  FileText,
  BarChart3,
  Settings,
  Shield,
  Database,
  Bell,
  Award,
  Calendar,
  Sparkles,
  Target,
  ClipboardList,
  ListOrdered,
  Layers,
} from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

interface NavItem {
  title: string;
  href: string;
  icon: LucideIcon;
  badge?: string;
}

export function Sidebar() {
  const location = useLocation();
  const { user } = useAuthStore();

  if (!user) return null;

  // Navigation items based on role
  const studentNavItems: NavItem[] = [
    { title: 'Dashboard', href: '/', icon: Home },
    { title: 'Chủ đề', href: '/sessions', icon: BookOpen },
    { title: 'Vocabulary', href: '/vocabulary', icon: Book },
    { title: 'Tests', href: '/tests', icon: FileText },
    { title: 'Progress', href: '/progress', icon: BarChart3 },
    { title: 'Achievements', href: '/achievements', icon: Award },
    { title: 'Profile', href: '/profile', icon: User },
  ];

  const teacherNavItems: NavItem[] = [
    { title: 'Dashboard', href: '/teacher', icon: Home },
    { title: 'My Students', href: '/teacher/students', icon: Users },
    { title: 'My Classes', href: '/teacher/classes', icon: GraduationCap },
    { title: 'Content Library', href: '/teacher/lessons', icon: BookOpen },
    { title: 'Grading Center', href: '/teacher/grading-center', icon: ClipboardList },
    { title: 'Create Lesson', href: '/teacher/create-lesson', icon: Sparkles },
    { title: 'Create Test', href: '/teacher/create-test', icon: FileText },
    { title: 'Curriculum', href: '/teacher/curriculum', icon: Book },
    { title: 'Analytics', href: '/teacher/analytics', icon: BarChart3 },
    { title: 'Schedule', href: '/teacher/schedule', icon: Calendar },
  ];

  const adminNavItems: NavItem[] = [
    { title: 'Dashboard', href: '/admin', icon: Home },
    { title: 'User Management', href: '/admin/users', icon: Users },
    { title: 'Content Hub', href: '/admin/content', icon: BookOpen },
    { title: 'Chủ đề học', href: '/admin/topics', icon: Target },
    { title: 'Sessions', href: '/admin/sessions', icon: Layers },
    { title: 'Lộ trình học', href: '/admin/curriculum', icon: ListOrdered },
    { title: 'Bài test', href: '/admin/tests', icon: FileText },
    { title: 'Classes', href: '/admin/classes', icon: GraduationCap },
    { title: 'AI Usage', href: '/admin/ai-usage', icon: Sparkles },
    { title: 'Face ID', href: '/admin/face-id', icon: Shield },
    { title: 'Analytics', href: '/admin/analytics', icon: BarChart3 },
    { title: 'Notifications', href: '/admin/notifications', icon: Bell },
    { title: 'System', href: '/admin/settings', icon: Database },
  ];

  const navItems = 
    user.role === 'student' ? studentNavItems :
    user.role === 'teacher' ? teacherNavItems :
    adminNavItems;

  const roleColors = {
    student: 'from-green-400 to-green-600',
    teacher: 'from-purple-400 to-purple-600',
    admin: 'from-red-400 to-red-600',
  };

  return (
    <div className="w-64 bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800 flex flex-col">
      {/* Logo */}
      <div className="p-6">
        <Link to="/" className="flex items-center gap-3">
          <div className={`flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-br ${roleColors[user.role]} text-white font-bold shadow-lg text-xl`}>
            E
          </div>
          <div>
            <div className="text-xl font-bold text-gray-900 dark:text-white">
              English AI
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-400 capitalize">
              {user.role}
            </div>
          </div>
        </Link>
      </div>

      <Separator />

      {/* Navigation */}
      <ScrollArea className="flex-1 px-3 py-4">
        <nav className="space-y-1">
          {navItems.map((item) => {
            const isActive = location.pathname === item.href;
            return (
              <Link key={item.href} to={item.href}>
                <Button
                  variant={isActive ? 'default' : 'ghost'}
                  className={`w-full justify-start gap-3 ${
                    isActive
                      ? 'bg-green-500 hover:bg-green-600 text-white'
                      : 'hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-700 dark:text-gray-300'
                  }`}
                >
                  <item.icon className="h-5 w-5" />
                  <span>{item.title}</span>
                  {item.badge && (
                    <span className="ml-auto text-xs bg-red-500 text-white px-2 py-0.5 rounded-full">
                      {item.badge}
                    </span>
                  )}
                </Button>
              </Link>
            );
          })}
        </nav>
      </ScrollArea>

      <Separator />

      {/* Settings */}
      <div className="p-3">
        <Link to={user.role === 'teacher' ? '/teacher/settings' : user.role === 'admin' ? '/admin/settings' : '/settings'}>
          <Button
            variant="ghost"
            className="w-full justify-start gap-3 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800"
          >
            <Settings className="h-5 w-5" />
            <span>Settings</span>
          </Button>
        </Link>
      </div>
    </div>
  );
}
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/app/components/ui/avatar';
import { Badge } from '@/app/components/ui/badge';
import { Separator } from '@/app/components/ui/separator';
import { motion } from 'motion/react';
import { 
  User, Mail, Crown, Zap, Flame, BookOpen, 
  Award, Settings, LogOut, ChevronRight, 
  Edit3, Shield, GraduationCap 
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import apiService from '@/services/apiService';
import type { AuthUser } from '@/types/auth';

interface UserProfile extends AuthUser {
  xp?: number;
  level?: number;
  streak?: number;
  totalLessonsCompleted?: number;
  badges?: string[];
  achievements?: Array<{
    title: string;
    description: string;
    unlockedAt: string;
  }>;
  createdAt?: string;
  lastActiveAt?: string;
}

export function Profile() {
  const navigate = useNavigate();
  const { user, logout, isAuthenticated } = useAuthStore();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    fetchUserProfile();
  }, [isAuthenticated, navigate]);

  const fetchUserProfile = async () => {
    try {
      setIsLoading(true);
      const response = await apiService.user.getMe();
      setProfile(response.data.data);
    } catch (error) {
      console.error('Failed to fetch profile:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const displayUser = profile || user;

  if (isLoading || !displayUser) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-purple-50 dark:from-gray-900 dark:via-gray-900 dark:to-gray-900 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500 mx-auto mb-4"></div>
          <p className="text-gray-600 dark:text-gray-400">Loading profile...</p>
        </div>
      </div>
    );
  }

  const getRoleBadge = (role: string) => {
    switch (role) {
      case 'admin':
        return (
          <Badge className="bg-purple-500 text-white">
            <Shield className="w-3 h-3 mr-1" />
            Admin
          </Badge>
        );
      case 'teacher':
        return (
          <Badge className="bg-blue-500 text-white">
            <GraduationCap className="w-3 h-3 mr-1" />
            Teacher
          </Badge>
        );
      default:
        return (
          <Badge className="bg-green-500 text-white">
            <BookOpen className="w-3 h-3 mr-1" />
            Student
          </Badge>
        );
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-purple-50 dark:from-gray-900 dark:via-gray-900 dark:to-gray-900 p-4 md:p-8">
      <div className="max-w-6xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          {/* Header */}
          <div className="flex flex-col md:flex-row items-start md:items-center justify-between mb-8">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
                My Profile
              </h1>
              <p className="text-gray-600 dark:text-gray-400">
                Manage your account and view your progress
              </p>
            </div>
            <div className="flex gap-3 mt-4 md:mt-0">
              <Button variant="outline" onClick={() => navigate('/')}>
                Back to Home
              </Button>
              <Button variant="destructive" onClick={handleLogout}>
                <LogOut className="w-4 h-4 mr-2" />
                Logout
              </Button>
            </div>
          </div>

          {/* Profile Overview */}
          <div className="grid md:grid-cols-3 gap-6 mb-8">
            {/* Avatar Card */}
            <Card className="p-6">
              <div className="text-center">
                <Avatar className="w-24 h-24 mx-auto mb-4">
                  <AvatarImage src={displayUser.avatar} />
                  <AvatarFallback className="bg-green-500 text-white text-2xl">
                    {displayUser.name?.[0]?.toUpperCase()}
                  </AvatarFallback>
                </Avatar>
                <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-1">
                  {displayUser.name}
                </h2>
                <p className="text-gray-500 dark:text-gray-400 mb-3">
                  {displayUser.email}
                </p>
                <div className="flex justify-center gap-2 mb-4">
                  {getRoleBadge(displayUser.role)}
                </div>
                <Button variant="outline" size="sm" className="w-full">
                  <Edit3 className="w-4 h-4 mr-2" />
                  Edit Profile
                </Button>
              </div>
            </Card>

            {/* Stats Grid */}
            <Card className="md:col-span-2 p-6">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center">
                <Crown className="w-5 h-5 mr-2 text-yellow-500" />
                Learning Statistics
              </h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="text-center p-4 bg-yellow-50 dark:bg-yellow-900/20 rounded-xl">
                  <Zap className="w-6 h-6 text-yellow-500 mx-auto mb-2" />
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">
                    {displayUser.xp || 0}
                  </p>
                  <p className="text-sm text-gray-500 dark:text-gray-400">Total XP</p>
                </div>
                <div className="text-center p-4 bg-orange-50 dark:bg-orange-900/20 rounded-xl">
                  <Flame className="w-6 h-6 text-orange-500 mx-auto mb-2" />
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">
                    {displayUser.streak || 0}
                  </p>
                  <p className="text-sm text-gray-500 dark:text-gray-400">Day Streak</p>
                </div>
                <div className="text-center p-4 bg-green-50 dark:bg-green-900/20 rounded-xl">
                  <BookOpen className="w-6 h-6 text-green-500 mx-auto mb-2" />
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">
                    {displayUser.totalLessonsCompleted || 0}
                  </p>
                  <p className="text-sm text-gray-500 dark:text-gray-400">Lessons</p>
                </div>
                <div className="text-center p-4 bg-purple-50 dark:bg-purple-900/20 rounded-xl">
                  <Award className="w-6 h-6 text-purple-500 mx-auto mb-2" />
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">
                    {displayUser.level || 1}
                  </p>
                  <p className="text-sm text-gray-500 dark:text-gray-400">Level</p>
                </div>
              </div>
            </Card>
          </div>

          {/* Badges & Achievements */}
          <div className="grid md:grid-cols-2 gap-6">
            {/* Badges */}
            <Card className="p-6">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                Badges Earned
              </h3>
              {displayUser.badges && displayUser.badges.length > 0 ? (
                <div className="flex flex-wrap gap-2">
                  {displayUser.badges.map((badge, index) => (
                    <Badge key={index} variant="secondary" className="px-3 py-1">
                      <Award className="w-3 h-3 mr-1" />
                      {badge}
                    </Badge>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 dark:text-gray-400 text-center py-4">
                  No badges yet. Start learning to earn badges!
                </p>
              )}
            </Card>

            {/* Quick Actions */}
            <Card className="p-6">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                Quick Actions
              </h3>
              <div className="space-y-2">
                <Button 
                  variant="outline" 
                  className="w-full justify-between"
                  onClick={() => navigate('/settings')}
                >
                  <span className="flex items-center">
                    <Settings className="w-4 h-4 mr-2" />
                    Settings
                  </span>
                  <ChevronRight className="w-4 h-4" />
                </Button>
                <Button 
                  variant="outline" 
                  className="w-full justify-between"
                  onClick={() => navigate('/lessons')}
                >
                  <span className="flex items-center">
                    <BookOpen className="w-4 h-4 mr-2" />
                    Continue Learning
                  </span>
                  <ChevronRight className="w-4 h-4" />
                </Button>
              </div>
            </Card>
          </div>
        </motion.div>
      </div>
    </div>
  );
}

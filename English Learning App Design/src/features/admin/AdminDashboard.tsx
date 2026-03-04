import { useEffect, useState } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { motion } from 'motion/react';
import { 
  Users, 
  BookOpen, 
  TrendingUp,
  DollarSign,
  Activity,
  AlertTriangle,
  CheckCircle,
  XCircle,
  Clock
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import apiService from '@/services/apiService';

export function AdminDashboard() {
  const { user } = useAuthStore();

  const [totalUsers, setTotalUsers] = useState(0);
  const [activeCourses, setActiveCourses] = useState(0);
  const [recentUsers, setRecentUsers] = useState<
    { name: string; email: string; role: string; time: string }[]
  >([]);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [usersRes, coursesRes] = await Promise.all([
          apiService.user.getAllUsers(),
          apiService.curriculum.getAll(),
        ]);
        const users = usersRes.data.data || [];
        const courses = coursesRes.data.data || [];
        setTotalUsers(users.length);
        setActiveCourses(courses.length);

        const mappedRecent = users
          .slice(0, 3)
          .map((u: any) => ({
            name: u.fullName || u.email,
            email: u.email,
            role: u.role || 'User',
            time: '',
          }));
        setRecentUsers(mappedRecent);
      } catch (e) {
        // swallow errors, show zeros
      }
    };

    loadData();
  }, []);

  const stats = [
    { label: 'Total Users', value: String(totalUsers), change: '', icon: Users, color: 'from-green-400 to-green-600' },
    { label: 'Active Courses', value: String(activeCourses), change: '', icon: BookOpen, color: 'from-blue-400 to-blue-600' },
    { label: 'Revenue', value: '$0', change: '', icon: DollarSign, color: 'from-purple-400 to-purple-600' },
    { label: 'Engagement', value: '-', change: '', icon: TrendingUp, color: 'from-orange-400 to-orange-600' },
  ];

  const systemStatus = [
    { label: 'API Server', status: 'operational', icon: CheckCircle, color: 'text-green-600' },
    { label: 'Database', status: 'operational', icon: CheckCircle, color: 'text-green-600' },
    { label: 'AI Service', status: 'operational', icon: CheckCircle, color: 'text-green-600' },
    { label: 'Storage', status: 'operational', icon: CheckCircle, color: 'text-green-600' },
  ];

  return (
    <div className="container mx-auto p-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
            Admin Dashboard
          </h1>
          <p className="text-gray-600 dark:text-gray-400">
            Platform overview and system management
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {stats.map((stat, index) => {
            const Icon = stat.icon;
            return (
              <motion.div
                key={stat.label}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 * index }}
              >
                <Card className="p-6 hover:shadow-lg transition-shadow">
                  <div className="flex items-center justify-between mb-4">
                    <div className={`flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-br ${stat.color} text-white shadow-lg`}>
                      <Icon className="h-6 w-6" />
                    </div>
                    <span className="text-sm font-semibold text-green-600 dark:text-green-400">
                      {stat.change}
                    </span>
                  </div>
                  <div className="text-3xl font-bold text-gray-900 dark:text-white mb-1">
                    {stat.value}
                  </div>
                  <div className="text-sm text-gray-600 dark:text-gray-400">
                    {stat.label}
                  </div>
                </Card>
              </motion.div>
            );
          })}
        </div>

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* System Status */}
          <div className="lg:col-span-2 space-y-6">
            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                <Activity className="h-5 w-5" />
                System Status
              </h2>
              <div className="grid grid-cols-2 gap-4">
                {systemStatus.map((system, index) => {
                  const Icon = system.icon;
                  return (
                    <motion.div
                      key={system.label}
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: 0.5 + 0.1 * index }}
                      className="flex items-center gap-3 p-4 bg-gray-50 dark:bg-gray-800 rounded-xl"
                    >
                      <Icon className={`h-5 w-5 ${system.color}`} />
                      <div>
                        <p className="font-semibold text-gray-900 dark:text-white">{system.label}</p>
                        <p className="text-sm text-gray-600 dark:text-gray-400 capitalize">{system.status}</p>
                      </div>
                    </motion.div>
                  );
                })}
              </div>
            </Card>

            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                <Clock className="h-5 w-5" />
                Recent User Registrations
              </h2>
              <div className="space-y-3">
                {recentUsers.map((user, index) => (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.8 + 0.1 * index }}
                    className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-800 rounded-xl"
                  >
                    <div>
                      <p className="font-semibold text-gray-900 dark:text-white">{user.name}</p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">{user.email}</p>
                    </div>
                    <div className="text-right">
                      <span className="inline-block px-2 py-1 bg-purple-100 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400 text-xs rounded-lg mb-1">
                        {user.role}
                      </span>
                      <p className="text-xs text-gray-500 dark:text-gray-500">{user.time}</p>
                    </div>
                  </motion.div>
                ))}
              </div>
            </Card>
          </div>

          {/* Quick Actions */}
          <div className="space-y-6">
            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">Quick Actions</h2>
              <div className="space-y-3">
                <Button className="w-full bg-purple-500 hover:bg-purple-600 text-white rounded-xl justify-start">
                  <Users className="h-4 w-4 mr-2" />
                  Manage Users
                </Button>
                <Button variant="outline" className="w-full rounded-xl justify-start">
                  <BookOpen className="h-4 w-4 mr-2" />
                  Manage Content
                </Button>
                <Button variant="outline" className="w-full rounded-xl justify-start">
                  <TrendingUp className="h-4 w-4 mr-2" />
                  View Analytics
                </Button>
                <Button variant="outline" className="w-full rounded-xl justify-start">
                  <Activity className="h-4 w-4 mr-2" />
                  System Logs
                </Button>
              </div>
            </Card>

            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">Today</h2>
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-gray-600 dark:text-gray-400">New Users</span>
                  <span className="font-bold text-green-600 dark:text-green-400">+24</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-gray-600 dark:text-gray-400">Active Sessions</span>
                  <span className="font-bold text-blue-600 dark:text-blue-400">342</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-gray-600 dark:text-gray-400">Lessons Completed</span>
                  <span className="font-bold text-purple-600 dark:text-purple-400">1,234</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-gray-600 dark:text-gray-400">Support Tickets</span>
                  <span className="font-bold text-orange-600 dark:text-orange-400">5</span>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </motion.div>
    </div>
  );
}

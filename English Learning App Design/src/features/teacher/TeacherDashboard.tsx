import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { motion } from 'motion/react';
import { 
  Users, 
  BookOpen, 
  BarChart3, 
  Clock,
  TrendingUp,
  Award,
  Calendar,
  MessageCircle
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';

export function TeacherDashboard() {
  const { user } = useAuthStore();

  const stats = [
    { label: 'Active Students', value: '156', icon: Users, color: 'from-green-400 to-green-600' },
    { label: 'Total Classes', value: '12', icon: BookOpen, color: 'from-blue-400 to-blue-600' },
    { label: 'Lessons Created', value: '45', icon: Award, color: 'from-purple-400 to-purple-600' },
    { label: 'Hours Taught', value: '127', icon: Clock, color: 'from-orange-400 to-orange-600' },
  ];

  const recentActivity = [
    { student: 'John Doe', action: 'Completed Lesson 5', time: '2 hours ago' },
    { student: 'Jane Smith', action: 'Submitted Assignment', time: '3 hours ago' },
    { student: 'Mike Johnson', action: 'Asked a question', time: '5 hours ago' },
    { student: 'Sarah Williams', action: 'Completed Pronunciation Practice', time: '1 day ago' },
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
            Welcome back, {user?.name}!
          </h1>
          <p className="text-gray-600 dark:text-gray-400">
            Here's what's happening with your classes today
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
          {/* Recent Activity */}
          <div className="lg:col-span-2">
            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                <TrendingUp className="h-5 w-5" />
                Recent Activity
              </h2>
              <div className="space-y-4">
                {recentActivity.map((activity, index) => (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.5 + 0.1 * index }}
                    className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-800 rounded-xl"
                  >
                    <div>
                      <p className="font-semibold text-gray-900 dark:text-white">{activity.student}</p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">{activity.action}</p>
                    </div>
                    <span className="text-xs text-gray-500 dark:text-gray-500">{activity.time}</span>
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
                <Button className="w-full bg-blue-500 hover:bg-blue-600 text-white rounded-xl justify-start">
                  <BookOpen className="h-4 w-4 mr-2" />
                  Create New Lesson
                </Button>
                <Button variant="outline" className="w-full rounded-xl justify-start">
                  <Users className="h-4 w-4 mr-2" />
                  View All Students
                </Button>
                <Button variant="outline" className="w-full rounded-xl justify-start">
                  <Calendar className="h-4 w-4 mr-2" />
                  Schedule Class
                </Button>
                <Button variant="outline" className="w-full rounded-xl justify-start">
                  <MessageCircle className="h-4 w-4 mr-2" />
                  Messages
                </Button>
              </div>
            </Card>

            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                <BarChart3 className="h-5 w-5" />
                This Week
              </h2>
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-gray-600 dark:text-gray-400">Classes Taught</span>
                  <span className="font-bold text-gray-900 dark:text-white">8</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-gray-600 dark:text-gray-400">Assignments Graded</span>
                  <span className="font-bold text-gray-900 dark:text-white">23</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-gray-600 dark:text-gray-400">Messages Sent</span>
                  <span className="font-bold text-gray-900 dark:text-white">45</span>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </motion.div>
    </div>
  );
}

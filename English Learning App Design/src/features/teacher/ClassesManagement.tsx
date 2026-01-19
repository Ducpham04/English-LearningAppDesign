import { useState } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Badge } from '@/app/components/ui/badge';
import { Progress } from '@/app/components/ui/progress';
import {
  Users,
  Plus,
  Calendar,
  BookOpen,
  TrendingUp,
  MoreVertical,
  Edit,
  Trash2,
  Eye,
  UserPlus,
  Settings,
  BarChart3,
} from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/app/components/ui/dropdown-menu';

interface Class {
  id: string;
  name: string;
  grade: string;
  students: number;
  maxStudents: number;
  schedule: string[];
  lessons: number;
  avgProgress: number;
  avgAccuracy: number;
  activeStudents: number;
  startDate: string;
  endDate: string;
  status: 'active' | 'upcoming' | 'completed';
}

export function ClassesManagement() {
  const classes: Class[] = [
    {
      id: '1',
      name: 'Class 10A - English Fundamentals',
      grade: 'Grade 10',
      students: 28,
      maxStudents: 30,
      schedule: ['Mon 8:00 AM', 'Wed 8:00 AM', 'Fri 8:00 AM'],
      lessons: 45,
      avgProgress: 72,
      avgAccuracy: 85,
      activeStudents: 25,
      startDate: '2025-09-01',
      endDate: '2026-06-30',
      status: 'active',
    },
    {
      id: '2',
      name: 'Class 10B - English Fundamentals',
      grade: 'Grade 10',
      students: 25,
      maxStudents: 30,
      schedule: ['Tue 10:00 AM', 'Thu 10:00 AM'],
      lessons: 42,
      avgProgress: 68,
      avgAccuracy: 80,
      activeStudents: 22,
      startDate: '2025-09-01',
      endDate: '2026-06-30',
      status: 'active',
    },
    {
      id: '3',
      name: 'Class 11A - Advanced English',
      grade: 'Grade 11',
      students: 22,
      maxStudents: 25,
      schedule: ['Mon 2:00 PM', 'Wed 2:00 PM', 'Fri 2:00 PM'],
      lessons: 38,
      avgProgress: 78,
      avgAccuracy: 88,
      activeStudents: 21,
      startDate: '2025-09-01',
      endDate: '2026-06-30',
      status: 'active',
    },
    {
      id: '4',
      name: 'Class 12A - Exam Preparation',
      grade: 'Grade 12',
      students: 0,
      maxStudents: 30,
      schedule: ['Mon 9:00 AM', 'Thu 9:00 AM'],
      lessons: 0,
      avgProgress: 0,
      avgAccuracy: 0,
      activeStudents: 0,
      startDate: '2026-02-01',
      endDate: '2026-06-30',
      status: 'upcoming',
    },
  ];

  const stats = {
    total: classes.length,
    active: classes.filter((c) => c.status === 'active').length,
    totalStudents: classes.reduce((sum, c) => sum + c.students, 0),
    avgProgress: Math.round(
      classes.filter((c) => c.status === 'active').reduce((sum, c) => sum + c.avgProgress, 0) /
        classes.filter((c) => c.status === 'active').length
    ),
  };

  const statusColors = {
    active: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    upcoming: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
    completed: 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400',
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-blue-400 to-blue-600 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
                <Users className="h-8 w-8" />
              </div>
              <div>
                <h1 className="text-3xl font-bold">Classes Management</h1>
                <p className="text-blue-50">Manage your classes and schedules</p>
              </div>
            </div>
            <Button className="bg-white text-blue-600 hover:bg-blue-50 gap-2">
              <Plus className="h-5 w-5" />
              Create Class
            </Button>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Users className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.total}</div>
                  <div className="text-sm text-blue-50">Total Classes</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <TrendingUp className="h-8 w-8 text-green-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.active}</div>
                  <div className="text-sm text-blue-50">Active</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <UserPlus className="h-8 w-8 text-purple-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.totalStudents}</div>
                  <div className="text-sm text-blue-50">Students</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <BarChart3 className="h-8 w-8 text-yellow-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.avgProgress}%</div>
                  <div className="text-sm text-blue-50">Avg Progress</div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto p-4 md:p-6">
        <div className="grid gap-4">
          {classes.map((classItem) => (
            <Card
              key={classItem.id}
              className="p-6 hover:shadow-lg transition-all duration-300"
            >
              <div className="flex flex-col lg:flex-row gap-6">
                {/* Class Info */}
                <div className="flex-1">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                        {classItem.name}
                      </h3>
                      <div className="flex flex-wrap gap-2 mb-3">
                        <Badge className={statusColors[classItem.status]}>
                          {classItem.status.charAt(0).toUpperCase() +
                            classItem.status.slice(1)}
                        </Badge>
                        <Badge variant="outline">{classItem.grade}</Badge>
                      </div>
                    </div>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="outline" size="sm">
                          <MoreVertical className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuItem className="gap-2">
                          <Eye className="h-4 w-4" />
                          View Details
                        </DropdownMenuItem>
                        <DropdownMenuItem className="gap-2">
                          <Edit className="h-4 w-4" />
                          Edit Class
                        </DropdownMenuItem>
                        <DropdownMenuItem className="gap-2">
                          <Settings className="h-4 w-4" />
                          Settings
                        </DropdownMenuItem>
                        <DropdownMenuItem className="gap-2 text-red-600">
                          <Trash2 className="h-4 w-4" />
                          Delete
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>

                  {/* Schedule */}
                  <div className="space-y-3">
                    <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                      <Calendar className="h-4 w-4" />
                      <span>
                        {new Date(classItem.startDate).toLocaleDateString()} -{' '}
                        {new Date(classItem.endDate).toLocaleDateString()}
                      </span>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      {classItem.schedule.map((time, index) => (
                        <Badge key={index} variant="outline" className="gap-1">
                          <Calendar className="h-3 w-3" />
                          {time}
                        </Badge>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Stats */}
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                  <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-2xl">
                    <div className="flex items-center gap-2 mb-2">
                      <Users className="h-4 w-4 text-blue-500" />
                      <span className="text-xs text-gray-500 dark:text-gray-400">
                        Students
                      </span>
                    </div>
                    <div className="text-2xl font-bold text-gray-900 dark:text-white">
                      {classItem.students}/{classItem.maxStudents}
                    </div>
                    <Progress
                      value={(classItem.students / classItem.maxStudents) * 100}
                      className="mt-2"
                    />
                  </div>

                  <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-2xl">
                    <div className="flex items-center gap-2 mb-2">
                      <TrendingUp className="h-4 w-4 text-green-500" />
                      <span className="text-xs text-gray-500 dark:text-gray-400">Active</span>
                    </div>
                    <div className="text-2xl font-bold text-green-600 dark:text-green-400">
                      {classItem.activeStudents}
                    </div>
                    <div className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                      {classItem.students > 0
                        ? Math.round((classItem.activeStudents / classItem.students) * 100)
                        : 0}
                      % active
                    </div>
                  </div>

                  <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-2xl">
                    <div className="flex items-center gap-2 mb-2">
                      <BookOpen className="h-4 w-4 text-purple-500" />
                      <span className="text-xs text-gray-500 dark:text-gray-400">Lessons</span>
                    </div>
                    <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">
                      {classItem.lessons}
                    </div>
                    <div className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                      completed
                    </div>
                  </div>

                  <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-2xl">
                    <div className="flex items-center gap-2 mb-2">
                      <BarChart3 className="h-4 w-4 text-orange-500" />
                      <span className="text-xs text-gray-500 dark:text-gray-400">
                        Progress
                      </span>
                    </div>
                    <div className="text-2xl font-bold text-orange-600 dark:text-orange-400">
                      {classItem.avgProgress}%
                    </div>
                    <Progress value={classItem.avgProgress} className="mt-2" />
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="flex gap-2 mt-6 pt-6 border-t border-gray-200 dark:border-gray-800">
                <Button variant="outline" size="sm" className="gap-2">
                  <Eye className="h-4 w-4" />
                  View Students
                </Button>
                <Button variant="outline" size="sm" className="gap-2">
                  <BookOpen className="h-4 w-4" />
                  View Lessons
                </Button>
                <Button variant="outline" size="sm" className="gap-2">
                  <BarChart3 className="h-4 w-4" />
                  Analytics
                </Button>
              </div>
            </Card>
          ))}
        </div>

        {classes.length === 0 && (
          <Card className="p-12 text-center">
            <div className="flex flex-col items-center gap-4">
              <div className="bg-gray-100 dark:bg-gray-800 p-6 rounded-full">
                <Users className="h-12 w-12 text-gray-400" />
              </div>
              <div>
                <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                  No classes yet
                </h3>
                <p className="text-gray-600 dark:text-gray-400 mb-4">
                  Create your first class to get started
                </p>
                <Button className="bg-blue-500 hover:bg-blue-600 gap-2">
                  <Plus className="h-5 w-5" />
                  Create Class
                </Button>
              </div>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
}

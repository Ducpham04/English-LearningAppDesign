import { useState } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/app/components/ui/select';
import {
  BarChart3,
  TrendingUp,
  TrendingDown,
  Users,
  Award,
  Target,
  Activity,
} from 'lucide-react';

export function TeacherAnalytics() {
  const [selectedClass, setSelectedClass] = useState('all');
  const [timeRange, setTimeRange] = useState('week');

  // Mock data
  const classes = [
    { id: 'all', name: 'All Classes' },
    { id: '1', name: 'Beginner A1' },
    { id: '2', name: 'Intermediate B1' },
    { id: '3', name: 'Advanced C1' },
  ];

  // Student progress
  const studentProgress = [
    { name: 'John Smith', completed: 15, total: 20, avgScore: 92 },
    { name: 'Sarah Johnson', completed: 18, total: 20, avgScore: 88 },
    { name: 'Mike Brown', completed: 12, total: 20, avgScore: 75 },
    { name: 'Emily Davis', completed: 16, total: 20, avgScore: 85 },
    { name: 'David Wilson', completed: 14, total: 20, avgScore: 78 },
  ];

  const stats = {
    totalStudents: 125,
    activeStudents: 98,
    avgCompletion: 85,
    avgScore: 84.5,
    improvement: 12,
  };

  const scoreDistribution = [
    { range: '90-100', count: 25 },
    { range: '80-89', count: 45 },
    { range: '70-79', count: 30 },
    { range: '60-69', count: 15 },
    { range: '0-59', count: 10 },
  ];

  const weeklyData = [
    { week: 'Week 1', score: 75, completion: 80 },
    { week: 'Week 2', score: 78, completion: 85 },
    { week: 'Week 3', score: 82, completion: 88 },
    { week: 'Week 4', score: 85, completion: 90 },
  ];

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-cyan-400 to-cyan-600 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
                <BarChart3 className="h-8 w-8" />
              </div>
              <div>
                <h1 className="text-3xl font-bold">Analytics & Insights</h1>
                <p className="text-cyan-50">Track student performance and engagement</p>
              </div>
            </div>
            <div className="flex gap-2">
              <Select value={selectedClass} onValueChange={setSelectedClass}>
                <SelectTrigger className="w-48 bg-white/20 border-white/30 text-white">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {classes.map(cls => (
                    <SelectItem key={cls.id} value={cls.id}>
                      {cls.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <Select value={timeRange} onValueChange={setTimeRange}>
                <SelectTrigger className="w-32 bg-white/20 border-white/30 text-white">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="week">This Week</SelectItem>
                  <SelectItem value="month">This Month</SelectItem>
                  <SelectItem value="quarter">This Quarter</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* Key Metrics */}
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Users className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.totalStudents}</div>
                  <div className="text-sm text-cyan-50">Students</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Activity className="h-8 w-8 text-green-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.activeStudents}</div>
                  <div className="text-sm text-cyan-50">Active</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Target className="h-8 w-8 text-yellow-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.avgCompletion}%</div>
                  <div className="text-sm text-cyan-50">Completion</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Award className="h-8 w-8 text-purple-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.avgScore.toFixed(1)}%</div>
                  <div className="text-sm text-cyan-50">Avg Score</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <TrendingUp className="h-8 w-8 text-green-400" />
                <div>
                  <div className="text-2xl font-bold">+{stats.improvement}%</div>
                  <div className="text-sm text-cyan-50">Improvement</div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto p-4 md:p-6 space-y-6">
        {/* Performance Trends */}
        <Card className="p-6">
          <h3 className="text-xl font-bold mb-4">Performance Trends</h3>
          <div className="space-y-4">
            {weeklyData.map((data, index) => (
              <div key={index} className="flex items-center gap-4">
                <div className="w-24 font-semibold text-gray-600 dark:text-gray-400">
                  {data.week}
                </div>
                <div className="flex-1 space-y-2">
                  <div className="flex items-center gap-2">
                    <span className="text-sm w-24">Avg Score:</span>
                    <div className="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                      <div
                        className="bg-blue-500 h-2 rounded-full"
                        style={{ width: `${data.score}%` }}
                      />
                    </div>
                    <span className="text-sm font-bold w-12">{data.score}%</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm w-24">Completion:</span>
                    <div className="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                      <div
                        className="bg-green-500 h-2 rounded-full"
                        style={{ width: `${data.completion}%` }}
                      />
                    </div>
                    <span className="text-sm font-bold w-12">{data.completion}%</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Student Progress Table */}
        <Card className="p-6">
          <h3 className="text-xl font-bold mb-4">Top Performers</h3>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-800">
                  <th className="text-left py-3 px-4 font-semibold">Student</th>
                  <th className="text-center py-3 px-4 font-semibold">Progress</th>
                  <th className="text-center py-3 px-4 font-semibold">Avg Score</th>
                  <th className="text-right py-3 px-4 font-semibold">Trend</th>
                </tr>
              </thead>
              <tbody>
                {studentProgress.map((student, index) => {
                  const completionPercent = (student.completed / student.total) * 100;
                  return (
                    <tr
                      key={index}
                      className="border-b border-gray-100 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-800"
                    >
                      <td className="py-3 px-4">
                        <div className="font-medium">{student.name}</div>
                        <div className="text-sm text-gray-500">
                          {student.completed}/{student.total} lessons
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <div className="flex items-center justify-center gap-2">
                          <div className="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-2 max-w-32">
                            <div
                              className="bg-cyan-500 h-2 rounded-full"
                              style={{ width: `${completionPercent}%` }}
                            />
                          </div>
                          <span className="text-sm font-medium">{completionPercent.toFixed(0)}%</span>
                        </div>
                      </td>
                      <td className="py-3 px-4 text-center">
                        <span className="font-bold text-lg">{student.avgScore}%</span>
                      </td>
                      <td className="py-3 px-4 text-right">
                        {student.avgScore >= 85 ? (
                          <TrendingUp className="h-5 w-5 text-green-500 inline" />
                        ) : (
                          <TrendingDown className="h-5 w-5 text-orange-500 inline" />
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Card>

        {/* Score Distribution */}
        <Card className="p-6">
          <h3 className="text-xl font-bold mb-4">Score Distribution</h3>
          <div className="grid grid-cols-5 gap-4">
            {scoreDistribution.map((dist, index) => (
              <div key={index} className="text-center">
                <div className="bg-cyan-100 dark:bg-cyan-900/30 rounded-lg p-4 mb-2">
                  <div className="text-3xl font-bold text-cyan-600 dark:text-cyan-400">
                    {dist.count}
                  </div>
                </div>
                <div className="text-sm font-medium">{dist.range}</div>
              </div>
            ))}
          </div>
        </Card>

        {/* Lesson Type Distribution */}
        <div className="grid md:grid-cols-2 gap-6">
          <Card className="p-6">
            <h3 className="text-xl font-bold mb-4">Lesson Type Distribution</h3>
            <div className="space-y-3">
              {[
                { name: 'Video', value: 35, color: 'bg-blue-500' },
                { name: 'Pronunciation', value: 25, color: 'bg-purple-500' },
                { name: 'AI Chat', value: 20, color: 'bg-green-500' },
                { name: 'Flashcard', value: 20, color: 'bg-orange-500' },
              ].map((type, index) => (
                <div key={index}>
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-sm font-medium">{type.name}</span>
                    <span className="text-sm font-bold">{type.value}%</span>
                  </div>
                  <div className="bg-gray-200 dark:bg-gray-700 rounded-full h-3">
                    <div
                      className={`${type.color} h-3 rounded-full`}
                      style={{ width: `${type.value}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </Card>

          <Card className="p-6">
            <h3 className="text-xl font-bold mb-4">Activity Overview</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                <div>
                  <div className="font-semibold">Total Lessons</div>
                  <div className="text-sm text-gray-500">This week</div>
                </div>
                <div className="text-2xl font-bold text-cyan-600 dark:text-cyan-400">247</div>
              </div>
              <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                <div>
                  <div className="font-semibold">Tests Completed</div>
                  <div className="text-sm text-gray-500">This week</div>
                </div>
                <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">45</div>
              </div>
              <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                <div>
                  <div className="font-semibold">Study Hours</div>
                  <div className="text-sm text-gray-500">This week</div>
                </div>
                <div className="text-2xl font-bold text-green-600 dark:text-green-400">156h</div>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}

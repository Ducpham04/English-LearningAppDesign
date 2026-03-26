import { useEffect, useMemo, useState } from 'react';
import { Card } from '@/app/components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/app/components/ui/select';
import {
  BarChart3,
  Users,
  Award,
  Target,
  Activity,
  Loader2,
  TrendingUp,
  TrendingDown,
} from 'lucide-react';
import apiService from '@/services/apiService';
import { toast } from 'sonner';

export function TeacherAnalytics() {
  const [selectedClass, setSelectedClass] = useState('all');
  const [timeRange, setTimeRange] = useState('week');
  const [classes, setClasses] = useState<{ id: string; name: string }[]>([]);
  const [loading, setLoading] = useState(true);

  const [classList, setClassList] = useState<any[]>([]);
  const [assignmentStats, setAssignmentStats] = useState<any[]>([]);

  useEffect(() => {
    apiService.teacherClasses.getAll()
      .then((res) => {
        const list = res.data?.data ?? [];
        const arr = Array.isArray(list) ? list : [];
        setClassList(arr);
        setClasses([
          { id: 'all', name: 'Tất cả lớp' },
          ...arr.map((c: any) => ({ id: String(c.id), name: c.name || `Lớp ${c.id}` })),
        ]);
      })
      .catch(() => {
        setClasses([{ id: 'all', name: 'Tất cả lớp' }]);
        toast.error('Không tải được danh sách lớp');
      })
      .finally(() => setLoading(false));
  }, []);

  // Fetch real assignment statistics for class(es) to build analytics tables.
  useEffect(() => {
    const fetchAssignmentStats = async () => {
      if (!classList || classList.length === 0) return;

      const classIds =
        selectedClass === 'all'
          ? classList.map((c) => Number(c.id))
          : [Number(selectedClass)];

      try {
        const lists = await Promise.all(
          classIds.map((classId) =>
            apiService.teacherClasses
              .getAssignmentStats(classId)
              .then((res) => res.data?.data ?? res.data ?? [])
              .catch(() => [])
          )
        );
        setAssignmentStats(lists.flat());
      } catch {
        toast.error('Không tải được thống kê bài tập');
        setAssignmentStats([]);
      }
    };

    fetchAssignmentStats();
  }, [selectedClass, classList]);

  const totalStudents = classList.reduce((sum, c) => sum + (c.studentCount ?? 0), 0);
  const totalAssignments = classList.reduce((sum, c) => sum + (c.assignmentCount ?? 0), 0);
  const stats = {
    totalStudents,
    activeStudents: classList.filter((c) => (c.status || '').toUpperCase() === 'ACTIVE').length,
    totalClasses: classList.length,
    totalAssignments,
    avgCompletion: classList.length ? Math.round((totalAssignments / Math.max(totalStudents, 1)) * 10) : 0,
    avgScore: 0,
    improvement: 0,
  };

  const filteredAssignmentStats = useMemo(() => {
    if (!assignmentStats || assignmentStats.length === 0) return [];

    const now = new Date();
    const dayMs = 24 * 60 * 60 * 1000;
    const days =
      timeRange === 'week' ? 7 : timeRange === 'month' ? 30 : 90;
    const start = new Date(now.getTime() - days * dayMs);

    return assignmentStats.filter((a: any) => {
      if (!a?.dueAt) return true;
      const due = new Date(a.dueAt);
      if (Number.isNaN(due.getTime())) return true;
      return due >= start && due <= now;
    });
  }, [assignmentStats, timeRange]);

  const studentProgress = useMemo(() => {
    const assignmentsCount = filteredAssignmentStats.length;
    const studentMap = new Map<
      string,
      { studentId: string; studentName: string; completed: number; scoreSum: number; scoreCount: number }
    >();

    filteredAssignmentStats.forEach((a: any) => {
      const list = Array.isArray(a?.students) ? a.students : [];
      list.forEach((s: any) => {
        const id = s?.studentId != null ? String(s.studentId) : '';
        if (!id) return;

        const prev =
          studentMap.get(id) ??
          { studentId: id, studentName: s?.studentName ?? 'Unknown', completed: 0, scoreSum: 0, scoreCount: 0 };

        if (s?.completed) {
          prev.completed += 1;
          if (typeof s?.score === 'number' && !Number.isNaN(s.score)) {
            prev.scoreSum += s.score;
            prev.scoreCount += 1;
          }
        }

        studentMap.set(id, prev);
      });
    });

    const rows = Array.from(studentMap.values()).map((r) => {
      const avgScore = r.scoreCount > 0 ? Math.round(r.scoreSum / r.scoreCount) : 0;
      return {
        studentId: r.studentId,
        name: r.studentName,
        completed: r.completed,
        total: Math.max(assignmentsCount, 1),
        avgScore,
      };
    });

    rows.sort(
      (a, b) =>
        b.completed / b.total - a.completed / a.total ||
        b.avgScore - a.avgScore
    );

    return rows.slice(0, 5);
  }, [filteredAssignmentStats]);

  const scoreDistribution = useMemo(() => {
    const dist = new Map<string, number>([
      ['90-100', 0],
      ['80-89', 0],
      ['70-79', 0],
      ['60-69', 0],
      ['0-59', 0],
    ]);

    filteredAssignmentStats.forEach((a: any) => {
      const list = Array.isArray(a?.students) ? a.students : [];
      list.forEach((s: any) => {
        if (!s?.completed) return;
        if (typeof s?.score !== 'number' || Number.isNaN(s.score)) return;
        const score = s.score;
        if (score >= 90) dist.set('90-100', (dist.get('90-100') ?? 0) + 1);
        else if (score >= 80) dist.set('80-89', (dist.get('80-89') ?? 0) + 1);
        else if (score >= 70) dist.set('70-79', (dist.get('70-79') ?? 0) + 1);
        else if (score >= 60) dist.set('60-69', (dist.get('60-69') ?? 0) + 1);
        else dist.set('0-59', (dist.get('0-59') ?? 0) + 1);
      });
    });

    return [
      { range: '90-100', count: dist.get('90-100') ?? 0 },
      { range: '80-89', count: dist.get('80-89') ?? 0 },
      { range: '70-79', count: dist.get('70-79') ?? 0 },
      { range: '60-69', count: dist.get('60-69') ?? 0 },
      { range: '0-59', count: dist.get('0-59') ?? 0 },
    ];
  }, [filteredAssignmentStats]);

  const uniqueStudentsCount = useMemo(() => {
    const ids = new Set<string>();
    filteredAssignmentStats.forEach((a: any) => {
      const list = Array.isArray(a?.students) ? a.students : [];
      list.forEach((s: any) => {
        const id = s?.studentId != null ? String(s.studentId) : '';
        if (id) ids.add(id);
      });
    });
    return ids.size;
  }, [filteredAssignmentStats]);

  const weeklyData = useMemo(() => {
    const assignmentsCount = filteredAssignmentStats.length;
    if (assignmentsCount === 0) {
      return [
        { week: 'Tuần 1', score: 0, completion: 0 },
        { week: 'Tuần 2', score: 0, completion: 0 },
        { week: 'Tuần 3', score: 0, completion: 0 },
        { week: 'Tuần 4', score: 0, completion: 0 },
      ];
    }

    // Derive overall completion + avg score from assignmentStats
    let completedItems = 0;
    let scoreSum = 0;
    let scoreCount = 0;
    const studentIdSet = new Set<string>();

    filteredAssignmentStats.forEach((a: any) => {
      const list = Array.isArray(a?.students) ? a.students : [];
      list.forEach((s: any) => {
        const id = s?.studentId != null ? String(s.studentId) : '';
        if (!id) return;
        studentIdSet.add(id);
        if (s?.completed) {
          completedItems += 1;
          if (typeof s?.score === 'number' && !Number.isNaN(s.score)) {
            scoreSum += s.score;
            scoreCount += 1;
          }
        }
      });
    });

    const uniqueStudents = Math.max(studentIdSet.size, 1);
    const overallCompletion = Math.round((completedItems / (uniqueStudents * assignmentsCount)) * 100);
    const overallAvgScore = scoreCount > 0 ? Math.round(scoreSum / scoreCount) : 0;
    const completionClamped = Math.max(0, Math.min(100, overallCompletion));
    const scoreClamped = Math.max(0, Math.min(100, overallAvgScore));

    return [
      { week: 'Tuần 1', score: scoreClamped, completion: completionClamped },
      { week: 'Tuần 2', score: scoreClamped, completion: completionClamped },
      { week: 'Tuần 3', score: scoreClamped, completion: completionClamped },
      { week: 'Tuần 4', score: scoreClamped, completion: completionClamped },
    ];
  }, [filteredAssignmentStats]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-950 flex items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-cyan-500" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
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

          {/* Key Metrics - dữ liệu từ API lớp học */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
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
                  <div className="text-2xl font-bold">{stats.totalClasses}</div>
                  <div className="text-sm text-cyan-50">Lớp</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Award className="h-8 w-8 text-purple-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.totalAssignments}</div>
                  <div className="text-sm text-cyan-50">Bài tập đã giao</div>
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

        {/* Real analytics summary (no mock data) */}
        <div className="grid md:grid-cols-2 gap-6">
          <Card className="p-6 md:col-span-2">
            <h3 className="text-xl font-bold mb-4">Analytics summary</h3>
            <div className="space-y-3">
              <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                <div>
                  <div className="font-semibold">Assignments in range</div>
                  <div className="text-sm text-gray-500">Filtered by dueAt</div>
                </div>
                <div className="text-2xl font-bold text-cyan-600 dark:text-cyan-400">{filteredAssignmentStats.length}</div>
              </div>
              <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                <div>
                  <div className="font-semibold">Unique students</div>
                  <div className="text-sm text-gray-500">Based on assignment-stats</div>
                </div>
                <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">{uniqueStudentsCount}</div>
              </div>
              <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                <div>
                  <div className="font-semibold">Overall completion</div>
                  <div className="text-sm text-gray-500">Completed / (students * assignments)</div>
                </div>
                <div className="text-2xl font-bold text-green-600 dark:text-green-400">{weeklyData?.[0]?.completion ?? 0}%</div>
              </div>
              <div className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                <div>
                  <div className="font-semibold">Overall avg score</div>
                  <div className="text-sm text-gray-500">Avg of completed scores</div>
                </div>
                <div className="text-2xl font-bold text-yellow-600 dark:text-yellow-400">{weeklyData?.[0]?.score ?? 0}%</div>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}

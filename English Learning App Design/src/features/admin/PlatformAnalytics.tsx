import { Card } from '@/app/components/ui/card';
import { Badge } from '@/app/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/app/components/ui/table';
import apiService from '@/services/apiService';
import { toast } from 'sonner';
import { useEffect, useMemo, useState } from 'react';
import {
  BarChart3,
  Users,
  BookOpen,
  Loader2,
  TrendingUp,
  Clock,
} from 'lucide-react';

type RecentUser = {
  id: string;
  fullName: string;
  email: string;
  role: string;
  createdAt: string | null;
};

type AdminStats = {
  totalUsers: number;
  totalStudents: number;
  totalTeachers: number;
  totalAdmins?: number;
  totalTopics: number;
  activeTopics?: number;
  totalSessions: number;
  recentUsers?: RecentUser[];
};

type AdminContentReport = {
  totalLessons: number;
  lessonsWithVideo: number;
  lessonsWithoutVideo: number;
  totalPronunciationAttempts: number;
  totalUsersWithAttempts: number;
};

export function PlatformAnalytics() {
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState<AdminStats | null>(null);
  const [report, setReport] = useState<AdminContentReport | null>(null);

  const recentUsers = useMemo(() => stats?.recentUsers ?? [], [stats]);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        setLoading(true);
        const [statsRes, reportRes] = await Promise.all([
          apiService.admin.getStats(),
          apiService.admin.content.getReport(),
        ]);

        const s = statsRes.data?.data ?? statsRes.data;
        const r = reportRes.data?.data ?? reportRes.data;

        if (cancelled) return;
        setStats(s ?? null);
        setReport(r ?? null);
      } catch (err: any) {
        const msg = err?.response?.data?.message ?? err?.message ?? 'Không tải được analytics';
        toast.error(msg);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => {
      cancelled = true;
    };
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-950 flex items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-cyan-500" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      <div className="bg-gradient-to-br from-orange-400 to-orange-600 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center gap-3 mb-6">
            <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
              <BarChart3 className="h-8 w-8" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Analytics & Reports (Admin)</h1>
              <p className="text-orange-50">Real data from backend endpoints</p>
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Users className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats?.totalUsers ?? '–'}</div>
                  <div className="text-sm text-orange-50">Total users</div>
                </div>
              </div>
            </Card>

            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <TrendingUp className="h-8 w-8 text-green-300" />
                <div>
                  <div className="text-2xl font-bold">{stats?.totalTeachers ?? '–'}</div>
                  <div className="text-sm text-orange-50">Teachers</div>
                </div>
              </div>
            </Card>

            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <BookOpen className="h-8 w-8 text-blue-300" />
                <div>
                  <div className="text-2xl font-bold">{stats?.totalTopics ?? '–'}</div>
                  <div className="text-sm text-orange-50">Topics</div>
                </div>
              </div>
            </Card>

            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Clock className="h-8 w-8 text-yellow-300" />
                <div>
                  <div className="text-2xl font-bold">{stats?.totalSessions ?? '–'}</div>
                  <div className="text-sm text-orange-50">Sessions</div>
                </div>
              </div>
            </Card>
          </div>

          {typeof stats?.activeTopics === 'number' && (
            <div className="mt-4 flex gap-2 items-center">
              <Badge className="bg-white/15 text-white border border-white/20">
                Active topics: {stats.activeTopics}
              </Badge>
            </div>
          )}
        </div>
      </div>

      <div className="max-w-7xl mx-auto p-4 md:p-6 space-y-6">
        <div className="grid gap-6 md:grid-cols-2">
          <Card className="p-6">
            <h2 className="text-lg font-bold mb-4">Recent users</h2>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Id</TableHead>
                  <TableHead>Name</TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Role</TableHead>
                  <TableHead>Created</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {recentUsers.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} className="text-muted-foreground">
                      No recent users
                    </TableCell>
                  </TableRow>
                ) : (
                  recentUsers.map((u) => (
                    <TableRow key={u.id}>
                      <TableCell className="whitespace-nowrap">{u.id}</TableCell>
                      <TableCell>{u.fullName}</TableCell>
                      <TableCell>{u.email}</TableCell>
                      <TableCell className="capitalize">{u.role}</TableCell>
                      <TableCell className="whitespace-nowrap">
                        {u.createdAt ? new Date(u.createdAt).toLocaleString() : '–'}
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </Card>

          <Card className="p-6">
            <h2 className="text-lg font-bold mb-4">Content report</h2>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Metric</TableHead>
                  <TableHead className="text-right">Value</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow>
                  <TableCell>Total lessons</TableCell>
                  <TableCell className="text-right">{report?.totalLessons ?? '–'}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Lessons with video</TableCell>
                  <TableCell className="text-right">{report?.lessonsWithVideo ?? '–'}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Lessons without video</TableCell>
                  <TableCell className="text-right">{report?.lessonsWithoutVideo ?? '–'}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Pronunciation attempts</TableCell>
                  <TableCell className="text-right">{report?.totalPronunciationAttempts ?? '–'}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Users with attempts</TableCell>
                  <TableCell className="text-right">{report?.totalUsersWithAttempts ?? '–'}</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </Card>
        </div>

        {/* Teacher/student specific details can be added later when backend provides time-series endpoints */}
      </div>
    </div>
  );
}

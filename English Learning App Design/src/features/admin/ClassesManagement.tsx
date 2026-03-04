import { useEffect, useState } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Badge } from '@/app/components/ui/badge';
import { Progress } from '@/app/components/ui/progress';
import {
  GraduationCap,
  Plus,
  Users,
  BookOpen,
  TrendingUp,
  Calendar,
  MoreVertical,
  Edit,
  Eye,
  Trash2,
} from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/app/components/ui/dropdown-menu';
import apiService from '@/services/apiService';
import { CreateClassModal } from '@/features/teacher/CreateClassModal';

interface AdminClassItem {
  id: string;
  name: string;
  teacher: string;
  students: number;
  maxStudents: number;
  progress: number;
  status: string;
  schedule: string[];
}

export function AdminClassesManagement() {
  const [classes, setClasses] = useState<AdminClassItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [openCreate, setOpenCreate] = useState(false);

  useEffect(() => {
    const fetchClasses = async () => {
      try {
        setLoading(true);
        const res = await apiService.classroom.getAll();
        const data = res.data.data || [];
        const mapped: AdminClassItem[] = data.map((c: any) => ({
          id: String(c.id),
          name: c.name,
          teacher: c.teacherName || 'Unknown',
          students: c.studentCount ?? 0,
          maxStudents: c.maxStudents ?? 30,
          progress: 0,
          status: c.status || 'active',
          schedule: [],
        }));
        setClasses(mapped);
      } finally {
        setLoading(false);
      }
    };

    fetchClasses();
  }, []);

  const stats = {
    total: classes.length,
    active: classes.filter((c) => c.status === 'active').length,
    students: classes.reduce((sum, c) => sum + c.students, 0),
    avgProgress: classes.length
      ? Math.round(classes.reduce((sum, c) => sum + c.progress, 0) / classes.length)
      : 0,
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      <div className="bg-gradient-to-br from-purple-400 to-purple-600 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
                <GraduationCap className="h-8 w-8" />
              </div>
              <div>
                <h1 className="text-3xl font-bold">Classes Management</h1>
                <p className="text-purple-50">Monitor all platform classes</p>
              </div>
            </div>
            <Button className="bg-white text-purple-600 hover:bg-purple-50 gap-2" onClick={() => setOpenCreate(true)}>
              <Plus className="h-5 w-5" />
              Create Class
            </Button>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <GraduationCap className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.total}</div>
                  <div className="text-sm text-purple-50">Total Classes</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <TrendingUp className="h-8 w-8 text-green-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.active}</div>
                  <div className="text-sm text-purple-50">Active</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Users className="h-8 w-8 text-blue-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.students}</div>
                  <div className="text-sm text-purple-50">Total Students</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <BookOpen className="h-8 w-8 text-yellow-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.avgProgress}%</div>
                  <div className="text-sm text-purple-50">Avg Progress</div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto p-4 md:p-6">
        {loading && (
          <div className="py-10 text-center text-gray-500 dark:text-gray-400">
            Loading classes...
          </div>
        )}

        <div className="grid gap-4">
          {classes.map((classItem) => (
            <Card key={classItem.id} className="p-6 hover:shadow-lg transition-all duration-300">
              <div className="flex flex-col lg:flex-row gap-6">
                <div className="flex-1">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                        {classItem.name}
                      </h3>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                        Teacher: {classItem.teacher}
                      </p>
                      <div className="flex flex-wrap gap-2">
                        {classItem.schedule.map((time, index) => (
                          <Badge key={index} variant="outline" className="gap-1">
                            <Calendar className="h-3 w-3" />
                            {time}
                          </Badge>
                        ))}
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
                        <DropdownMenuItem className="gap-2 text-red-600">
                          <Trash2 className="h-4 w-4" />
                          Delete
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                </div>

                <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                  <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-2xl">
                    <div className="flex items-center gap-2 mb-2">
                      <Users className="h-4 w-4 text-blue-500" />
                      <span className="text-xs text-gray-500 dark:text-gray-400">Students</span>
                    </div>
                    <div className="text-2xl font-bold text-gray-900 dark:text-white">
                      {classItem.students}/{classItem.maxStudents}
                    </div>
                    <Progress value={(classItem.students / classItem.maxStudents) * 100} className="mt-2" />
                  </div>

                  <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-2xl">
                    <div className="flex items-center gap-2 mb-2">
                      <BookOpen className="h-4 w-4 text-purple-500" />
                      <span className="text-xs text-gray-500 dark:text-gray-400">Progress</span>
                    </div>
                    <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">
                      {classItem.progress}%
                    </div>
                    <Progress value={classItem.progress} className="mt-2" />
                  </div>

                  <div className="bg-gray-50 dark:bg-gray-800 p-4 rounded-2xl">
                    <div className="flex items-center gap-2 mb-2">
                      <TrendingUp className="h-4 w-4 text-green-500" />
                      <span className="text-xs text-gray-500 dark:text-gray-400">Status</span>
                    </div>
                    <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400">
                      {classItem.status.charAt(0).toUpperCase() + classItem.status.slice(1)}
                    </Badge>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}

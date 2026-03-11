import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Badge } from '@/app/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/app/components/ui/tabs';
import { ArrowLeft, Users, BookOpen, ListTodo, LayoutDashboard, Loader2, Plus, Trash2 } from 'lucide-react';
import apiService from '@/services/apiService';
import { toast } from 'sonner';

export function ClassDetail() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const id = classId ? Number(classId) : NaN;
  const [detail, setDetail] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id || isNaN(id)) return;
    apiService.teacherClasses.getById(id)
      .then((res) => setDetail(res.data?.data ?? null))
      .catch(() => toast.error('Không tải được thông tin lớp'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading || !detail) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-950 flex items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-blue-500" />
      </div>
    );
  }

  const classInfo = detail.classInfo ?? detail;
  const course = detail.course;
  const sessions = detail.sessions ?? [];
  const lessons = detail.lessons ?? [];
  const students = detail.students ?? [];
  const assignments = detail.assignments ?? [];

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      <div className="bg-gradient-to-br from-blue-400 to-blue-600 text-white p-4 md:p-6">
        <div className="max-w-7xl mx-auto">
          <Button variant="ghost" className="text-white hover:bg-white/20 gap-2 mb-4" onClick={() => navigate('/teacher/classes')}>
            <ArrowLeft className="h-4 w-4" />
            Quay lại
          </Button>
          <h1 className="text-2xl md:text-3xl font-bold">{classInfo.name}</h1>
          <p className="text-blue-50 mt-1">
            {course?.title && `${course.title} • `}
            {classInfo.studentCount ?? 0} học viên • {classInfo.assignmentCount ?? 0} bài tập
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto p-4 md:p-6">
        <Tabs defaultValue="overview" className="space-y-4">
          <TabsList className="grid w-full grid-cols-4 lg:w-auto lg:inline-grid">
            <TabsTrigger value="overview" className="gap-2">
              <LayoutDashboard className="h-4 w-4" />
              Tổng quan
            </TabsTrigger>
            <TabsTrigger value="curriculum" className="gap-2">
              <BookOpen className="h-4 w-4" />
              Lộ trình
            </TabsTrigger>
            <TabsTrigger value="assignments" className="gap-2">
              <ListTodo className="h-4 w-4" />
              Bài tập
            </TabsTrigger>
            <TabsTrigger value="students" className="gap-2">
              <Users className="h-4 w-4" />
              Học viên
            </TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="space-y-4">
            <Card className="p-6">
              <h3 className="font-semibold text-lg mb-4">Thông tin lớp</h3>
              <dl className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                <div>
                  <dt className="text-gray-500 dark:text-gray-400">Khóa học</dt>
                  <dd>{course?.title ?? '—'}</dd>
                </div>
                <div>
                  <dt className="text-gray-500 dark:text-gray-400">Trạng thái</dt>
                  <dd>
                    <Badge variant="outline">
                      {classInfo.status === 'ACTIVE' ? 'Đang hoạt động' : classInfo.status === 'UPCOMING' ? 'Sắp diễn ra' : 'Kết thúc'}
                    </Badge>
                  </dd>
                </div>
                <div>
                  <dt className="text-gray-500 dark:text-gray-400">Ngày bắt đầu / kết thúc</dt>
                  <dd>
                    {classInfo.startDate ? new Date(classInfo.startDate).toLocaleDateString('vi-VN') : '—'} –{' '}
                    {classInfo.endDate ? new Date(classInfo.endDate).toLocaleDateString('vi-VN') : '—'}
                  </dd>
                </div>
                <div>
                  <dt className="text-gray-500 dark:text-gray-400">Số học viên / Bài tập</dt>
                  <dd>{classInfo.studentCount ?? 0} / {classInfo.assignmentCount ?? 0}</dd>
                </div>
              </dl>
            </Card>
          </TabsContent>

          <TabsContent value="curriculum" className="space-y-4">
            <Card className="p-6">
              <h3 className="font-semibold text-lg mb-4">Session & Lesson (khóa học)</h3>
              {sessions.length === 0 && lessons.length === 0 ? (
                <p className="text-gray-500 dark:text-gray-400 text-sm">Chưa có session/lesson trong khóa học.</p>
              ) : (
                <div className="space-y-4">
                  {sessions.map((s: any) => (
                    <div key={s.id} className="border rounded-lg p-4">
                      <h4 className="font-medium">{s.title}</h4>
                      <p className="text-xs text-gray-500 mt-1">{s.lessonCount ?? 0} bài</p>
                    </div>
                  ))}
                  {lessons.length > 0 && (
                    <div>
                      <h4 className="font-medium mb-2">Tất cả bài học ({lessons.length})</h4>
                      <ul className="list-disc list-inside text-sm text-gray-600 dark:text-gray-400">
                        {lessons.slice(0, 20).map((l: any) => (
                          <li key={l.id}>{l.title}</li>
                        ))}
                        {lessons.length > 20 && <li>... và {lessons.length - 20} bài khác</li>}
                      </ul>
                    </div>
                  )}
                </div>
              )}
            </Card>
          </TabsContent>

          <TabsContent value="assignments" className="space-y-4">
            <Card className="p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold text-lg">Bài tập đã giao</h3>
              </div>
              {assignments.length === 0 ? (
                <p className="text-gray-500 dark:text-gray-400 text-sm">Chưa giao bài nào.</p>
              ) : (
                <ul className="space-y-3">
                  {assignments.map((a: any) => (
                    <li key={a.id} className="flex items-center justify-between p-3 rounded-lg bg-gray-50 dark:bg-gray-800">
                      <div>
                        <p className="font-medium">{a.title}</p>
                        {a.dueAt && (
                          <p className="text-xs text-gray-500">Hạn: {new Date(a.dueAt).toLocaleString('vi-VN')}</p>
                        )}
                      </div>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-red-600"
                        onClick={() => {
                          if (confirm('Xóa bài tập này?')) {
                            apiService.teacherClasses.deleteAssignment(id, a.id)
                              .then(() => {
                                setDetail((d: any) => ({ ...d, assignments: (d.assignments ?? []).filter((x: any) => x.id !== a.id) }));
                                toast.success('Đã xóa');
                              })
                              .catch(() => toast.error('Không xóa được'));
                          }
                        }}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </li>
                  ))}
                </ul>
              )}
            </Card>
          </TabsContent>

          <TabsContent value="students" className="space-y-4">
            <Card className="p-6">
              <h3 className="font-semibold text-lg mb-4">Danh sách học viên</h3>
              {students.length === 0 ? (
                <p className="text-gray-500 dark:text-gray-400 text-sm">Chưa có học viên trong lớp.</p>
              ) : (
                <ul className="space-y-2">
                  {students.map((s: any) => (
                    <li key={s.studentId} className="flex items-center justify-between p-3 rounded-lg bg-gray-50 dark:bg-gray-800">
                      <div>
                        <p className="font-medium">{s.fullName}</p>
                        <p className="text-xs text-gray-500">{s.email}</p>
                      </div>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-red-600"
                        onClick={() => {
                          if (confirm(`Xóa ${s.fullName} khỏi lớp?`)) {
                            apiService.teacherClasses.removeStudent(id, s.studentId)
                              .then(() => {
                                setDetail((d: any) => ({ ...d, students: (d.students ?? []).filter((x: any) => x.studentId !== s.studentId) }));
                                toast.success('Đã xóa');
                              })
                              .catch(() => toast.error('Không xóa được'));
                          }
                        }}
                      >
                        Xóa
                      </Button>
                    </li>
                  ))}
                </ul>
              )}
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}

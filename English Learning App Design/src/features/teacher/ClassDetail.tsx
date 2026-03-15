import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Badge } from '@/app/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/app/components/ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/app/components/ui/dialog';
import { Label } from '@/app/components/ui/label';
import { Input } from '@/app/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/app/components/ui/select';
import { ArrowLeft, Users, BookOpen, ListTodo, LayoutDashboard, Loader2, Plus, Trash2 } from 'lucide-react';
import apiService from '@/services/apiService';
import { toast } from 'sonner';

export function ClassDetail() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const id = classId ? Number(classId) : NaN;
  const [detail, setDetail] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [assignModalOpen, setAssignModalOpen] = useState(false);
  const [assignLessonId, setAssignLessonId] = useState<string>('');
  const [assignTitle, setAssignTitle] = useState('');
  const [assignDueAt, setAssignDueAt] = useState('');
  const [assignSubmitting, setAssignSubmitting] = useState(false);
  const [studentModalOpen, setStudentModalOpen] = useState(false);
  const [studentIdInput, setStudentIdInput] = useState('');
  const [studentSubmitting, setStudentSubmitting] = useState(false);
  const [usersList, setUsersList] = useState<{ id: string; email?: string; fullName?: string; role?: string }[]>([]);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [deleteSubmitting, setDeleteSubmitting] = useState(false);

  const loadDetail = useCallback(() => {
    if (!id || isNaN(id)) return;
    setLoading(true);
    apiService.teacherClasses.getById(id)
      .then((res) => setDetail(res.data?.data ?? null))
      .catch(() => toast.error('Không tải được thông tin lớp'))
      .finally(() => setLoading(false));
  }, [id]);

  useEffect(() => {
    loadDetail();
  }, [loadDetail]);

  useEffect(() => {
    if (studentModalOpen) {
      apiService.auth.getUsers()
        .then((res) => {
          const list = res.data?.data ?? [];
          setUsersList(Array.isArray(list) ? list : []);
        })
        .catch(() => setUsersList([]));
    }
  }, [studentModalOpen]);

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

  const handleDeleteClass = () => {
    if (!id || isNaN(id)) return;
    setDeleteSubmitting(true);
    apiService.teacherClasses.delete(id)
      .then(() => {
        toast.success('Đã xoá lớp.');
        navigate('/teacher/classes');
      })
      .catch((e: any) => {
        toast.error(e?.response?.data?.message || 'Không thể xoá lớp.');
        setDeleteConfirmOpen(false);
      })
      .finally(() => setDeleteSubmitting(false));
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      <div className="bg-gradient-to-br from-blue-400 to-blue-600 text-white p-4 md:p-6">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-wrap items-center justify-between gap-4 mb-4">
            <Button variant="ghost" className="text-white hover:bg-white/20 gap-2" onClick={() => navigate('/teacher/classes')}>
              <ArrowLeft className="h-4 w-4" />
              Quay lại
            </Button>
            <Button
              variant="outline"
              className="border-white/50 text-white hover:bg-red-500/20 hover:border-red-300 gap-2"
              onClick={() => setDeleteConfirmOpen(true)}
            >
              <Trash2 className="h-4 w-4" />
              Xoá lớp
            </Button>
          </div>
          <h1 className="text-2xl md:text-3xl font-bold">{classInfo.name}</h1>
          <p className="text-blue-50 mt-1">
            {course?.title && `${course.title} • `}
            {classInfo.studentCount ?? 0} học viên • {classInfo.assignmentCount ?? 0} bài tập
          </p>
        </div>
      </div>

      <Dialog open={deleteConfirmOpen} onOpenChange={setDeleteConfirmOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Xác nhận xoá lớp</DialogTitle>
          </DialogHeader>
          <p className="text-gray-600 dark:text-gray-400">
            Bạn có chắc muốn xoá lớp &quot;{classInfo.name}&quot;? Hành động này không thể hoàn tác. Toàn bộ bài tập và danh sách học viên của lớp sẽ bị xoá.
          </p>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteConfirmOpen(false)} disabled={deleteSubmitting}>
              Huỷ
            </Button>
            <Button variant="destructive" onClick={handleDeleteClass} disabled={deleteSubmitting}>
              {deleteSubmitting ? <Loader2 className="h-4 w-4 animate-spin" /> : <Trash2 className="h-4 w-4" />}
              {deleteSubmitting ? 'Đang xoá...' : 'Xoá lớp'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

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
              <div className="flex items-center justify-between mb-4 flex-wrap gap-2">
                <h3 className="font-semibold text-lg">Bài tập đã giao</h3>
                <Button
                  size="sm"
                  className="gap-2"
                  onClick={() => {
                    setAssignLessonId('');
                    setAssignTitle('');
                    setAssignDueAt('');
                    setAssignModalOpen(true);
                  }}
                  disabled={lessons.length === 0}
                >
                  <Plus className="h-4 w-4" /> Thêm bài tập
                </Button>
              </div>
              {assignments.length === 0 ? (
                <p className="text-gray-500 dark:text-gray-400 text-sm">
                  {lessons.length === 0 ? 'Chọn khóa học có bài học để giao bài tập.' : 'Chưa giao bài nào.'}
                </p>
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
                              .then(() => { loadDetail(); toast.success('Đã xóa'); })
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
              <div className="flex items-center justify-between mb-4 flex-wrap gap-2">
                <h3 className="font-semibold text-lg">Danh sách học viên</h3>
                <Button size="sm" className="gap-2" onClick={() => { setStudentIdInput(''); setStudentModalOpen(true); }}>
                  <Plus className="h-4 w-4" /> Thêm học viên
                </Button>
              </div>
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
                              .then(() => { loadDetail(); toast.success('Đã xóa'); })
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

      {/* Modal: Thêm bài tập */}
      <Dialog open={assignModalOpen} onOpenChange={setAssignModalOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Giao bài tập</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div>
              <Label>Bài học *</Label>
              <Select value={assignLessonId} onValueChange={(v) => { setAssignLessonId(v); const l = lessons.find((x: any) => String(x.id) === v); if (l) setAssignTitle(l.title || ''); }}>
                <SelectTrigger><SelectValue placeholder="Chọn bài học" /></SelectTrigger>
                <SelectContent>
                  {lessons.map((l: any) => (
                    <SelectItem key={l.id} value={String(l.id)}>{l.title}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label>Tên bài tập (tùy chọn)</Label>
              <Input value={assignTitle} onChange={(e) => setAssignTitle(e.target.value)} placeholder="Mặc định = tên bài học" />
            </div>
            <div>
              <Label>Hạn nộp (tùy chọn)</Label>
              <Input type="datetime-local" value={assignDueAt} onChange={(e) => setAssignDueAt(e.target.value)} />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setAssignModalOpen(false)}>Hủy</Button>
            <Button
              disabled={!assignLessonId || assignSubmitting}
              onClick={async () => {
                setAssignSubmitting(true);
                try {
                  await apiService.teacherClasses.createAssignment(id, {
                    lessonId: Number(assignLessonId),
                    title: assignTitle.trim() || undefined,
                    dueAt: assignDueAt || undefined,
                  });
                  toast.success('Đã giao bài tập');
                  setAssignModalOpen(false);
                  loadDetail();
                } catch (e: any) {
                  toast.error(e?.response?.data?.message || 'Giao bài thất bại');
                } finally {
                  setAssignSubmitting(false);
                }
              }}
            >
              {assignSubmitting ? 'Đang giao...' : 'Giao bài'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Modal: Thêm học viên */}
      <Dialog open={studentModalOpen} onOpenChange={setStudentModalOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Thêm học viên vào lớp</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div>
              <Label>Chọn tài khoản (hoặc nhập ID)</Label>
              <Select value={studentIdInput} onValueChange={setStudentIdInput}>
                <SelectTrigger><SelectValue placeholder="Chọn học viên" /></SelectTrigger>
                <SelectContent>
                  {usersList
                    .filter((u: any) => u.role === 'STUDENT' || !u.role)
                    .map((u: any) => (
                      <SelectItem key={u.id} value={u.id}>{u.fullName || u.email || u.id}</SelectItem>
                    ))}
                </SelectContent>
              </Select>
              <p className="text-xs text-gray-500 mt-1">Nếu không thấy, nhập ID tài khoản bên dưới.</p>
            </div>
            <div>
              <Label>Hoặc nhập ID tài khoản (userId)</Label>
              <Input value={studentIdInput} onChange={(e) => setStudentIdInput(e.target.value)} placeholder="VD: user-uuid" />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setStudentModalOpen(false)}>Hủy</Button>
            <Button
              disabled={!studentIdInput.trim() || studentSubmitting}
              onClick={async () => {
                const sid = studentIdInput.trim();
                if (!sid) return;
                setStudentSubmitting(true);
                try {
                  await apiService.teacherClasses.addStudent(id, sid);
                  toast.success('Đã thêm học viên');
                  setStudentModalOpen(false);
                  loadDetail();
                } catch (e: any) {
                  toast.error(e?.response?.data?.message || 'Thêm học viên thất bại');
                } finally {
                  setStudentSubmitting(false);
                }
              }}
            >
              {studentSubmitting ? 'Đang thêm...' : 'Thêm'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

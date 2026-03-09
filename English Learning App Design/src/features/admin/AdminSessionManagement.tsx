import { useState, useEffect } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Label } from '@/app/components/ui/label';
import { Badge } from '@/app/components/ui/badge';
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription,
} from '@/app/components/ui/dialog';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from '@/app/components/ui/select';
import { BookOpen, Plus, Pencil, Trash2, Layers, Search, Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import apiService from '@/services/apiService';

interface Session {
  id: number;
  title: string;
  orderIndex: number;
  courseId: number;
  courseTitle: string;
  lessonCount: number;
}

interface Course {
  id: number;
  title: string;
  description?: string;
}

const emptyForm = { title: '', courseId: '', orderIndex: '1' };

export function AdminSessionManagement() {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filterCourse, setFilterCourse] = useState<string>('all');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingSession, setEditingSession] = useState<Session | null>(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [sessRes, courseRes] = await Promise.all([
        apiService.session.getAll(),
        apiService.curriculum.getAll(),
      ]);
      setSessions((sessRes.data?.data as Session[]) ?? []);
      setCourses((courseRes.data?.data as Course[]) ?? []);
    } catch {
      toast.error('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    setEditingSession(null);
    setForm(emptyForm);
    setModalOpen(true);
  };

  const openEdit = (s: Session) => {
    setEditingSession(s);
    setForm({ title: s.title, courseId: String(s.courseId), orderIndex: String(s.orderIndex) });
    setModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.title || !form.courseId) {
      toast.error('Vui lòng điền đủ thông tin');
      return;
    }
    setSaving(true);
    try {
      const payload = {
        title: form.title,
        courseId: Number(form.courseId),
        orderIndex: Number(form.orderIndex) || 1,
      };
      if (editingSession) {
        await apiService.session.update(editingSession.id, payload);
        toast.success('Session đã được cập nhật');
      } else {
        await apiService.session.create(payload);
        toast.success('Session đã được tạo thành công 🎉');
      }
      setModalOpen(false);
      fetchData();
    } catch {
      toast.error('Lỗi khi lưu session');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Xóa session này? Tất cả lessons trong session cũng sẽ bị ảnh hưởng.')) return;
    try {
      await apiService.session.delete(id);
      toast.success('Đã xóa session');
      setSessions(sessions.filter(s => s.id !== id));
    } catch {
      toast.error('Lỗi khi xóa session');
    }
  };

  const filtered = sessions.filter(s => {
    const matchSearch = s.title.toLowerCase().includes(search.toLowerCase());
    const matchCourse = filterCourse === 'all' || String(s.courseId) === filterCourse;
    return matchSearch && matchCourse;
  });

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-violet-500 to-violet-700 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
                <Layers className="h-8 w-8" />
              </div>
              <div>
                <h1 className="text-3xl font-bold">Session Management</h1>
                <p className="text-violet-100">Tạo và quản lý các session học tập</p>
              </div>
            </div>
            <Button
              onClick={openCreate}
              className="bg-white text-violet-700 hover:bg-violet-50 gap-2 font-semibold"
            >
              <Plus className="h-5 w-5" />
              Tạo Session
            </Button>
          </div>

          <div className="grid grid-cols-3 gap-4 mt-4">
            {[
              { label: 'Tổng Sessions', value: sessions.length },
              { label: 'Courses', value: courses.length },
              { label: 'Sessions hôm nay', value: sessions.filter(() => true).length },
            ].map((stat) => (
              <Card key={stat.label} className="bg-white/10 backdrop-blur-sm border-white/20 p-4 text-white">
                <div className="text-2xl font-bold">{stat.value}</div>
                <div className="text-sm text-violet-100">{stat.label}</div>
              </Card>
            ))}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto p-4 md:p-6">
        {/* Filters */}
        <Card className="p-4 mb-6">
          <div className="flex flex-col md:flex-row gap-3">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
              <Input
                placeholder="Tìm theo tên session..."
                value={search}
                onChange={e => setSearch(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={filterCourse} onValueChange={setFilterCourse}>
              <SelectTrigger className="w-[200px]">
                <SelectValue placeholder="Tất cả courses" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả courses</SelectItem>
                {courses.map(c => (
                  <SelectItem key={c.id} value={String(c.id)}>{c.title}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </Card>

        {/* Session List */}
        {loading ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="h-8 w-8 animate-spin text-violet-500" />
          </div>
        ) : filtered.length === 0 ? (
          <Card className="p-12 text-center">
            <Layers className="h-12 w-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500 font-medium">
              {sessions.length === 0 ? 'Chưa có session nào. Tạo session đầu tiên!' : 'Không tìm thấy session phù hợp.'}
            </p>
            {sessions.length === 0 && (
              <Button onClick={openCreate} className="mt-4 bg-violet-600 hover:bg-violet-700 text-white gap-2">
                <Plus className="h-4 w-4" /> Tạo Session ngay
              </Button>
            )}
          </Card>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {filtered.map(session => (
              <Card key={session.id} className="p-5 hover:shadow-lg transition-all duration-300 group">
                <div className="flex items-start justify-between mb-3">
                  <div className="bg-violet-100 dark:bg-violet-900/30 p-2 rounded-xl">
                    <BookOpen className="h-6 w-6 text-violet-600 dark:text-violet-400" />
                  </div>
                  <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                    <Button size="sm" variant="ghost" onClick={() => openEdit(session)}>
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button size="sm" variant="ghost" className="text-red-500" onClick={() => handleDelete(session.id)}>
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
                <h3 className="font-semibold text-gray-900 dark:text-white mb-1 line-clamp-2">{session.title}</h3>
                <p className="text-sm text-gray-500 dark:text-gray-400 mb-3">{session.courseTitle || 'Chưa có course'}</p>
                <div className="flex items-center gap-2">
                  <Badge variant="outline" className="text-xs">Thứ tự: {session.orderIndex}</Badge>
                  <Badge variant="outline" className="text-xs">{session.lessonCount} bài học</Badge>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* Create/Edit Modal */}
      <Dialog open={modalOpen} onOpenChange={setModalOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{editingSession ? 'Cập nhật Session' : 'Tạo Session mới'}</DialogTitle>
            <DialogDescription>
              {editingSession ? 'Chỉnh sửa thông tin session' : 'Điền thông tin để tạo session học tập mới'}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSave} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="title">Tên Session *</Label>
              <Input
                id="title"
                value={form.title}
                onChange={e => setForm({ ...form, title: e.target.value })}
                placeholder="VD: Chương 1 - Giới thiệu"
                required
              />
            </div>

            <div className="space-y-2">
              <Label>Course *</Label>
              <Select value={form.courseId} onValueChange={v => setForm({ ...form, courseId: v })}>
                <SelectTrigger>
                  <SelectValue placeholder="Chọn course" />
                </SelectTrigger>
                <SelectContent>
                  {courses.map(c => (
                    <SelectItem key={c.id} value={String(c.id)}>{c.title}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {courses.length === 0 && (
                <p className="text-xs text-orange-500">Chưa có course nào. Vui lòng tạo course trước.</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="orderIndex">Thứ tự chương</Label>
              <Input
                id="orderIndex"
                type="number"
                min="1"
                value={form.orderIndex}
                onChange={e => setForm({ ...form, orderIndex: e.target.value })}
              />
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Hủy</Button>
              <Button type="submit" disabled={saving} className="bg-violet-600 hover:bg-violet-700 text-white gap-2">
                {saving && <Loader2 className="h-4 w-4 animate-spin" />}
                {editingSession ? 'Cập nhật' : 'Tạo Session'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

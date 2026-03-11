import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { ArrowLeft, BookOpen, Loader2, ExternalLink } from 'lucide-react';
import apiService from '@/services/apiService';
import { toast } from 'sonner';

export function StudentClassDetail() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const id = classId ? Number(classId) : NaN;
  const [assignments, setAssignments] = useState<any[]>([]);
  const [classInfo, setClassInfo] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id || isNaN(id)) return;
    Promise.all([
      apiService.student.getClassAssignments(id),
      apiService.student.getClasses(),
    ])
      .then(([assignRes, classesRes]) => {
        const list = assignRes.data?.data ?? [];
        setAssignments(Array.isArray(list) ? list : []);
        const classes = classesRes.data?.data ?? [];
        const found = (Array.isArray(classes) ? classes : []).find((c: any) => c.id === id);
        setClassInfo(found || { name: 'Lớp học', id });
      })
      .catch(() => {
        toast.error('Không tải được thông tin lớp hoặc bạn chưa ghi danh lớp này.');
        setAssignments([]);
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-950 flex items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-emerald-500" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20">
      <div className="bg-gradient-to-br from-emerald-400 to-teal-600 text-white p-4 md:p-6">
        <div className="max-w-4xl mx-auto">
          <Button variant="ghost" className="text-white hover:bg-white/20 gap-2 mb-4" onClick={() => navigate('/')}>
            <ArrowLeft className="h-4 w-4" />
            Về trang chủ
          </Button>
          <h1 className="text-2xl md:text-3xl font-bold">{classInfo?.name ?? 'Lớp học'}</h1>
          <p className="text-emerald-50 mt-1">Bài tập giáo viên giao</p>
        </div>
      </div>

      <div className="max-w-4xl mx-auto p-4 md:p-6">
        {assignments.length === 0 ? (
          <Card className="p-8 text-center">
            <BookOpen className="h-12 w-12 text-gray-400 mx-auto mb-3" />
            <p className="text-gray-500 dark:text-gray-400">Chưa có bài tập nào trong lớp này.</p>
          </Card>
        ) : (
          <ul className="space-y-3">
            {assignments.map((a: any) => (
              <Card key={a.id} className="p-4">
                <div className="flex items-start justify-between gap-4">
                  <div className="min-w-0 flex-1">
                    <h3 className="font-semibold text-gray-900 dark:text-white">{a.title}</h3>
                    {a.description && (
                      <p className="text-sm text-gray-500 dark:text-gray-400 mt-1 line-clamp-2">{a.description}</p>
                    )}
                    {a.dueAt && (
                      <p className="text-xs text-gray-500 mt-2">Hạn: {new Date(a.dueAt).toLocaleString('vi-VN')}</p>
                    )}
                  </div>
                  <Button
                    size="sm"
                    className="gap-1 shrink-0"
                    onClick={() => a.lessonId && navigate(`/lessons/${a.lessonId}`)}
                  >
                    <ExternalLink className="h-4 w-4" />
                    Vào bài
                  </Button>
                </div>
              </Card>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

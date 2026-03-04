import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAppStore } from '@/store/appStore';
import { Card } from '@/app/components/ui/card';
import { Loader2 } from 'lucide-react';
import apiService from '@/services/apiService';

interface LessonInfo {
  id: number;
  title: string;
  courseId: number;
  sessionId: number;
  type: 'VIDEO' | 'QUIZ' | 'VOCAB' | 'SPEAKING';
  orderIndex: number;
  contentPayload: Record<string, any>;
}

/**
 * LessonRouter - Component trung gian để route đến đúng loại bài học
 * Sử dụng khi click vào lesson từ SessionDetail
 */
export function LessonRouter() {
  const { lessonId } = useParams<{ lessonId: string }>();
  const navigate = useNavigate();
  const { selectedLessonId } = useAppStore();
  const [loading, setLoading] = useState(true);
  const [lesson, setLesson] = useState<LessonInfo | null>(null);

  useEffect(() => {
    const fetchLesson = async () => {
      const id = lessonId || selectedLessonId;
      
      if (!id) {
        navigate('/sessions');
        return;
      }

      try {
        // Try to fetch from API
        const response = await apiService.lesson.getById(Number(id));
        const lessonData = response.data.data;
        setLesson(lessonData);
      } catch (err) {
        console.error('Failed to fetch lesson:', err);
        navigate('/sessions');
        return;
      } finally {
        setLoading(false);
      }
    };

    fetchLesson();
  }, [lessonId, selectedLessonId, navigate]);

  useEffect(() => {
    if (!lesson || loading) return;

    console.log('LessonRouter - Navigating to lesson type:', lesson.type);
    switch (lesson.type) {
      case 'VIDEO':
        navigate(`/lessons/video-conversation/${lesson.id}`, { replace: true });
        break;
      case 'SPEAKING':
      case 'pronunciation':
        navigate(`/lessons/pronunciation-lesson/${lesson.id}`, { replace: true });
        break;
      case 'VOCAB':
      case 'flashcard':
        navigate(`/lessons/flashcard-lesson/${lesson.id}`, { replace: true });
        break;
      case 'QUIZ':
        navigate(`/lessons/quiz/${lesson.id}`, { replace: true });
        break;
      default:
        navigate('/sessions');
    }
  }, [lesson, loading, navigate]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Card className="p-8 text-center">
          <Loader2 className="h-12 w-12 animate-spin mx-auto mb-4 text-green-500" />
          <p className="text-lg font-semibold">Đang tải bài học...</p>
        </Card>
      </div>
    );
  }

  return null;
}
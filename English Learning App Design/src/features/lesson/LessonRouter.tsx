import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAppStore } from '@/store/appStore';
import { getLessonById } from '@/services/mockData';
import { Card } from '@/app/components/ui/card';
import { Loader2 } from 'lucide-react';

/**
 * LessonRouter - Component trung gian để route đến đúng loại bài học
 * Sử dụng khi click vào lesson từ SessionDetail
 */
export function LessonRouter() {
  const { lessonId } = useParams<{ lessonId: string }>();
  const navigate = useNavigate();
  const { selectedLessonId } = useAppStore();

  useEffect(() => {
    const id = lessonId || selectedLessonId;
    
    console.log('LessonRouter - lessonId from URL:', lessonId);
    console.log('LessonRouter - selectedLessonId from store:', selectedLessonId);
    console.log('LessonRouter - using id:', id);
    
    if (!id) {
      console.log('LessonRouter - No ID found, redirecting to sessions');
      navigate('/sessions');
      return;
    }

    const lesson = getLessonById(id);
    
    console.log('LessonRouter - Found lesson:', lesson);

    if (!lesson) {
      console.log('LessonRouter - Lesson not found, redirecting to sessions');
      navigate('/sessions');
      return;
    }

    // Route đến đúng loại bài học dựa trên lesson type
    console.log('LessonRouter - Navigating to lesson type:', lesson.type);
    switch (lesson.type) {
      case 'video':
        console.log('LessonRouter - Navigating to video-conversation');
        navigate(`/lessons/video-conversation/${id}`, { replace: true });
        break;
      case 'pronunciation':
        console.log('LessonRouter - Navigating to pronunciation-lesson');
        navigate(`/lessons/pronunciation-lesson/${id}`, { replace: true });
        break;
      case 'ai-conversation':
        console.log('LessonRouter - Navigating to ai-conversation-lesson');
        navigate(`/lessons/ai-conversation-lesson/${id}`, { replace: true });
        break;
      case 'flashcard':
        console.log('LessonRouter - Navigating to flashcard-lesson');
        navigate(`/lessons/flashcard-lesson/${id}`, { replace: true });
        break;
      default:
        console.log('LessonRouter - Unknown lesson type, redirecting to sessions');
        navigate('/sessions');
    }
  }, [lessonId, selectedLessonId, navigate]);

  return (
    <div className="flex min-h-screen items-center justify-center">
      <Card className="p-8 text-center">
        <Loader2 className="h-12 w-12 animate-spin mx-auto mb-4 text-green-500" />
        <p className="text-lg font-semibold">Đang tải bài học...</p>
      </Card>
    </div>
  );
}
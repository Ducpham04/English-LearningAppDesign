import { Button } from '@/app/components/ui/button';
import { Progress } from '@/app/components/ui/progress';
import { LevelBadge } from '@/shared/ui/LevelBadge';
import { LessonItem } from '@/features/lesson/LessonItem';
import { useAppStore } from '@/store/appStore';
import { getLessonsBySessionId, getSessionById } from '@/services/mockData';
import { ArrowLeft, Clock, BookOpen } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';

export function SessionDetail() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const { selectedSessionId, setCurrentView } = useAppStore();
  const navigate = useNavigate();

  // Use sessionId from URL params first, fallback to store
  const currentSessionId = sessionId || selectedSessionId;

  console.log('SessionDetail - sessionId from URL:', sessionId);
  console.log('SessionDetail - selectedSessionId from store:', selectedSessionId);
  console.log('SessionDetail - using currentSessionId:', currentSessionId);

  if (!currentSessionId) {
    return <div>Session not found</div>;
  }

  const session = getSessionById(currentSessionId);
  const lessons = getLessonsBySessionId(currentSessionId);

  if (!session) {
    return <div>Session not found</div>;
  }

  const progress = (session.completedLessons / session.totalLessons) * 100;

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <Button
        variant="ghost"
        size="sm"
        className="mb-4"
        onClick={() => {
          console.log('SessionDetail - Back button clicked, navigating to /sessions');
          navigate('/sessions');
        }}
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Quay lại
      </Button>

      {/* Session Info */}
      <div className="mb-8 rounded-lg bg-gradient-to-br from-blue-500 to-purple-600 p-8 text-white">
        <div className="mb-3">
          <LevelBadge level={session.level} />
        </div>
        <h1 className="mb-3 text-3xl font-bold">{session.title}</h1>
        <p className="mb-6 text-lg opacity-90">{session.description}</p>

        {/* Stats */}
        <div className="mb-6 flex flex-wrap gap-6">
          <div className="flex items-center gap-2">
            <BookOpen className="h-5 w-5" />
            <span>{session.totalLessons} bài học</span>
          </div>
          <div className="flex items-center gap-2">
            <Clock className="h-5 w-5" />
            <span>~{session.estimatedTime} phút</span>
          </div>
        </div>

        {/* Progress */}
        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span>Tiến độ</span>
            <span>{session.completedLessons}/{session.totalLessons} bài</span>
          </div>
          <Progress value={progress} className="h-3 bg-white/20" />
        </div>
      </div>

      {/* Lessons List */}
      <div className="space-y-4">
        <h2 className="mb-4 text-2xl font-bold">Danh sách bài học</h2>
        {lessons.map((lesson) => (
          <LessonItem
            key={lesson.id}
            lesson={lesson}
            onClick={() => {
              console.log('SessionDetail - Clicked lesson:', lesson.id, lesson.title);
              console.log('SessionDetail - Navigating to:', `/lessons/${lesson.id}`);
              navigate(`/lessons/${lesson.id}`);
            }}
          />
        ))}
      </div>
    </div>
  );
}
import { useState } from 'react';
import { Button } from '@/app/components/ui/button';
import { Progress } from '@/app/components/ui/progress';
import { LevelBadge } from '@/shared/ui/LevelBadge';
import { LessonItem } from '@/features/lesson/LessonItem';
import { useAppStore } from '@/store/appStore';
import { useLessonsBySession, useSessionById, type SessionInfo, type LessonInfo } from '@/hooks/useStudentData';
import { ArrowLeft, Clock, BookOpen } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';

export function SessionDetail() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const { selectedSessionId } = useAppStore();
  const navigate = useNavigate();

  // Use sessionId from URL params first, fallback to store
  const currentSessionId = sessionId || selectedSessionId;
  const numericSessionId = currentSessionId ? Number(currentSessionId) : null;
  const { session, loading: sessionLoading, error: sessionError } = useSessionById(numericSessionId);

  // Fetch lessons for this session
  const { lessons, loading: lessonsLoading, error: lessonsError } = useLessonsBySession(numericSessionId || 0);

  if (sessionLoading || lessonsLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500"></div>
      </div>
    );
  }

  if (!currentSessionId || !session) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Button
          variant="ghost"
          size="sm"
          className="mb-4"
          onClick={() => navigate('/sessions')}
        >
          <ArrowLeft className="mr-2 h-4 w-4" />
          Quay lại
        </Button>
        <p className="text-red-500">Không tìm thấy session.</p>
      </div>
    );
  }

  const completedLessons = 0; // TODO: Get from user progress
  const progress = (completedLessons / (session.lessonCount || 1)) * 100;

  const mapLessonType = (backendType: LessonInfo['type']) => {
    switch (backendType) {
      case 'VIDEO':
        return 'video' as const;
      case 'SPEAKING':
        return 'pronunciation' as const;
      case 'VOCAB':
        return 'flashcard' as const;
      case 'QUIZ':
        return 'flashcard' as const;
      default:
        return 'video' as const;
    }
  };

  // Map API lessons to FE format
  const mappedLessons = lessons.map((lesson: LessonInfo) => ({
    id: String(lesson.id),
    sessionId: String(lesson.sessionId),
    title: lesson.title,
    type: mapLessonType(lesson.type),
    description: '',
    duration: lesson.contentPayload?.duration || 25,
    status: 'available' as any,
    order: lesson.orderIndex || 0,
  }));

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <Button
        variant="ghost"
        size="sm"
        className="mb-4"
        onClick={() => navigate('/sessions')}
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Quay lại
      </Button>

      {/* Session Info */}
      <div className="mb-8 rounded-lg bg-gradient-to-br from-blue-500 to-purple-600 p-8 text-white">
        <div className="mb-3">
          <LevelBadge level="beginner" />
        </div>
        <h1 className="mb-3 text-3xl font-bold">{session.title}</h1>
        <p className="mb-6 text-lg opacity-90">{session.courseName || ''}</p>

        {/* Stats */}
        <div className="mb-6 flex flex-wrap gap-6">
          <div className="flex items-center gap-2">
            <BookOpen className="h-5 w-5" />
            <span>{session.lessonCount || 0} bài học</span>
          </div>
          <div className="flex items-center gap-2">
            <Clock className="h-5 w-5" />
            <span>~{(session.lessonCount || 0) * 25} phút</span>
          </div>
        </div>

        {/* Progress */}
        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span>Tiến độ</span>
            <span>{completedLessons}/{session.lessonCount || 0} bài</span>
          </div>
          <Progress value={progress} className="h-3 bg-white/20" />
        </div>
      </div>

      {/* Lessons List */}
      <div className="space-y-4">
        <h2 className="mb-4 text-2xl font-bold">Danh sách bài học</h2>
        {mappedLessons.map((lesson) => (
          <LessonItem
            key={lesson.id}
            lesson={lesson}
            onClick={() => navigate(`/lessons/${lesson.id}`)}
          />
        ))}
      </div>
    </div>
  );
}
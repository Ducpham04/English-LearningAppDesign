import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useSessionById, useLessonsBySession } from '@/hooks/useStudentData';
import apiService from '@/services/apiService';
import { useAuthStore } from '@/store/authStore';
import { useAppStore } from '@/store/appStore';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Badge } from '@/app/components/ui/badge';
import { Progress } from '@/app/components/ui/progress';
import {
  ArrowLeft, Clock, BookOpen, Play, Mic, Brain, MessageCircle,
  Lock, CheckCircle, ChevronRight, Loader2,
} from 'lucide-react';

// Map type backend → icon/label/color
const LESSON_TYPE_META: Record<string, {
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  color: string;
  bg: string;
  route: string;
}> = {
  VIDEO: {
    label: 'Video',
    icon: Play,
    color: 'text-blue-600',
    bg: 'bg-blue-100 dark:bg-blue-900',
    route: 'video-conversation',
  },
  SPEAKING: {
    label: 'Phát âm',
    icon: Mic,
    color: 'text-green-600',
    bg: 'bg-green-100 dark:bg-green-900',
    route: 'pronunciation-lesson',
  },
  VOCAB: {
    label: 'Từ vựng',
    icon: Brain,
    color: 'text-orange-600',
    bg: 'bg-orange-100 dark:bg-orange-900',
    route: 'flashcard-lesson',
  },
  QUIZ: {
    label: 'Hội thoại AI',
    icon: MessageCircle,
    color: 'text-purple-600',
    bg: 'bg-purple-100 dark:bg-purple-900',
    route: 'ai-conversation-lesson',
  },
};

const DEFAULT_META = LESSON_TYPE_META.VIDEO;

export function SessionDetail() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();
  const idNum = sessionId ? Number(sessionId) : null;

  const { session, loading: sessionLoading, refetch: refetchSession } = useSessionById(idNum);
  const { lessons, loading: lessonsLoading, refetch: refetchLessons } = useLessonsBySession(idNum ?? 0);
  const [completedLessonIds, setCompletedLessonIds] = useState<Set<number>>(new Set());
  const isAuthenticated = useAuthStore((s) => !!s.tokens?.accessToken);
  const progressVersion = useAppStore((s) => s.progressVersion);

  useEffect(() => {
    if (!idNum || !isAuthenticated) return;
    apiService.student
      .getCompletedLessonIds()
      .then((res) => {
        try {
          const list = res?.data?.data;
          setCompletedLessonIds(new Set(Array.isArray(list) ? list : []));
        } catch {
          setCompletedLessonIds(new Set());
        }
      })
      .catch(() => setCompletedLessonIds(new Set()));
  }, [idNum, isAuthenticated, progressVersion]);

  const lessonsWithStatus = lessons.map((l: any) => ({
    ...l,
    status: completedLessonIds.has(l.id) ? 'completed' : (l.status ?? undefined),
  }));

  if (!idNum) return <div className="container mx-auto px-4 py-8">Session không tồn tại</div>;

  if (sessionLoading && !session) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-blue-500" />
      </div>
    );
  }

  if (!session) {
    return (
      <div className="container mx-auto px-4 py-8 text-center">
        <p className="text-gray-500">Không tìm thấy session</p>
        <Button onClick={() => navigate('/sessions')} className="mt-4">Quay lại</Button>
      </div>
    );
  }

  const totalLessons = lessonsWithStatus.length || session.lessonCount || 0;
  const completedLessons = lessonsWithStatus.filter((l: any) => l.status === 'completed').length;
  const progress = totalLessons > 0 ? (completedLessons / totalLessons) * 100 : 0;
  const estimatedTime = lessonsWithStatus.reduce((sum: number, l: any) => sum + (l.durationMinutes ?? 5), 0) || totalLessons * 5;

  return (
    <div className="container mx-auto max-w-3xl px-4 py-8">
      {/* Back */}
      <Button variant="ghost" size="sm" className="mb-4 gap-2" onClick={() => navigate('/sessions')}>
        <ArrowLeft className="h-4 w-4" />
        Quay lại
      </Button>

      {/* Session Hero */}
      <div className="mb-8 rounded-2xl bg-gradient-to-br from-blue-500 to-purple-600 p-6 text-white shadow-lg">
        <h1 className="mb-2 text-2xl font-bold leading-snug">{session.title}</h1>
        {(session as any).topicName && (
          <p className="mb-4 text-sm opacity-80">Chủ đề: {(session as any).topicName}</p>
        )}
        <div className="mb-4 flex flex-wrap gap-4 text-sm">
          <div className="flex items-center gap-1.5 opacity-90">
            <BookOpen className="h-4 w-4" />
            <span>{totalLessons} bài học</span>
          </div>
          <div className="flex items-center gap-1.5 opacity-90">
            <Clock className="h-4 w-4" />
            <span>~{estimatedTime} phút</span>
          </div>
        </div>
        <div className="space-y-1.5">
          <div className="flex justify-between text-xs opacity-80">
            <span>Tiến độ</span>
            <span>{completedLessons}/{totalLessons} bài hoàn thành</span>
          </div>
          <Progress value={progress} className="h-2 bg-white/20" />
        </div>
      </div>

      {/* Lessons List */}
      <div className="space-y-3">
        <h2 className="text-xl font-bold">Danh sách bài học</h2>

        {lessonsLoading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
          </div>
        ) : lessons.length === 0 ? (
          <Card className="p-8 text-center text-gray-400">
            <BookOpen className="mx-auto mb-3 h-10 w-10 opacity-30" />
            <p>Chưa có bài học nào trong session này.</p>
          </Card>
        ) : (
          lessonsWithStatus
            .slice()
            .sort((a: any, b: any) => (a.orderIndex ?? 0) - (b.orderIndex ?? 0))
            .map((lesson: any, idx: number) => {
              const meta = LESSON_TYPE_META[lesson.type] ?? DEFAULT_META;
              const Icon = meta.icon;
              const isCompleted = lesson.status === 'completed';
              const isLocked = lesson.status === 'locked';

              return (
                <Card
                  key={lesson.id}
                  className={`flex cursor-pointer items-center gap-4 p-4 transition-all hover:shadow-md ${
                    isLocked ? 'opacity-60 cursor-not-allowed' : 'hover:border-blue-300'
                  } ${isCompleted ? 'border-green-400 border-2' : ''}`}
                  onClick={() => {
                    if (isLocked) return;
                    navigate(`/lessons/${lesson.id}`);
                  }}
                >
                  {/* Icon */}
                  <div className={`flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full ${meta.bg}`}>
                    {isLocked ? (
                      <Lock className={`h-5 w-5 ${meta.color}`} />
                    ) : isCompleted ? (
                      <CheckCircle className="h-5 w-5 text-green-500" />
                    ) : (
                      <Icon className={`h-5 w-5 ${meta.color}`} />
                    )}
                  </div>

                  {/* Content */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-0.5">
                      <span className="text-xs text-gray-400 font-medium">Bài {idx + 1}</span>
                      <Badge variant="outline" className={`text-xs py-0 h-5 ${meta.color} border-current`}>
                        {meta.label}
                      </Badge>
                      {lesson.s3Status === 'PROCESSING' && (
                        <Badge className="text-xs py-0 h-5 bg-yellow-100 text-yellow-700 border-yellow-300 animate-pulse">
                          ⏳ Đang xử lý
                        </Badge>
                      )}
                      {lesson.s3Status === 'COMPLETED' && lesson.type === 'VIDEO' && (
                        <Badge className="text-xs py-0 h-5 bg-green-100 text-green-700 border-green-300">
                          ✓ Script sẵn
                        </Badge>
                      )}
                    </div>
                    <h3 className="font-semibold truncate">{lesson.title}</h3>
                    {lesson.description && (
                      <p className="text-sm text-gray-500 dark:text-gray-400 truncate mt-0.5">{lesson.description}</p>
                    )}
                    <div className="flex items-center gap-1 mt-1 text-xs text-gray-400">
                      <Clock className="h-3 w-3" />
                      <span>{lesson.durationMinutes ?? 5} phút</span>
                    </div>
                  </div>

                  {/* Arrow */}
                  {!isLocked && (
                    <ChevronRight className="h-5 w-5 flex-shrink-0 text-gray-300" />
                  )}
                </Card>
              );
            })
        )}
      </div>
    </div>
  );
}

import { useState, useEffect } from 'react';
import { SessionCard } from './SessionCard';
import { useAppStore } from '@/store/appStore';
import { useSessionsByCourse, type SessionInfo } from '@/hooks/useStudentData';
import { useNavigate, useParams } from 'react-router-dom';

interface SessionOverviewProps {
  courseId?: number;
}

export function SessionOverview({ courseId }: SessionOverviewProps) {
  const { setSelectedSession } = useAppStore();
  const navigate = useNavigate();
  const params = useParams();
  
  // Use API if courseId is provided, otherwise use store
  const courseIdNum = courseId || Number(params.courseId) || 1;
  const { sessions: apiSessions, loading, error } = useSessionsByCourse(courseIdNum);

  const handleSessionClick = (sessionId: string | number) => {
    setSelectedSession(String(sessionId));
    navigate(`/sessions/${sessionId}`);
  };

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <p className="text-red-500">Lỗi: {error}</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="mb-2 text-3xl font-bold">Lộ trình học</h1>
        <p className="text-gray-600 dark:text-gray-400">
          Chọn một chủ đề để bắt đầu hành trình học tiếng Anh của bạn
        </p>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {apiSessions.map((session: SessionInfo) => (
          <SessionCard
            key={session.id}
            session={{
              id: String(session.id),
              title: session.title,
              description: session.courseName || '',
              level: 'beginner' as any,
              thumbnail: undefined,
              totalLessons: session.lessonCount || 0,
              completedLessons: 0,
              estimatedTime: 25,
              status: 'available' as any,
              order: session.orderIndex || 0,
            }}
            onClick={() => handleSessionClick(session.id)}
          />
        ))}
      </div>
    </div>
  );
}
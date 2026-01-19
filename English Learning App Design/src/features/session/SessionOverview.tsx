import { SessionCard } from './SessionCard';
import { useAppStore } from '@/store/appStore';
import { useNavigate } from 'react-router-dom';

export function SessionOverview() {
  const { sessions, setSelectedSession } = useAppStore();
  const navigate = useNavigate();

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="mb-2 text-3xl font-bold">Lộ trình học</h1>
        <p className="text-gray-600 dark:text-gray-400">
          Chọn một chủ đề để bắt đầu hành trình học tiếng Anh của bạn
        </p>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {sessions.map((session) => (
          <SessionCard
            key={session.id}
            session={session}
            onClick={() => {
              console.log('SessionOverview - Clicked session:', session.id, session.title);
              setSelectedSession(session.id);
              navigate(`/sessions/${session.id}`);
            }}
          />
        ))}
      </div>
    </div>
  );
}
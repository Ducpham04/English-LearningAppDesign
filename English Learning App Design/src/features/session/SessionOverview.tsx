import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '@/services/apiService';
import { useAppStore } from '@/store/appStore';
import { BookOpen, Clock, Layers, ChevronRight, Search, CheckCircle } from 'lucide-react';
import { Input } from '@/app/components/ui/input';
import { Badge } from '@/app/components/ui/badge';
import { toast } from 'sonner';

interface TopicSession {
  id: number;
  title: string;
  orderIndex: number;
  lessonCount: number;
}

interface Topic {
  id: number;
  name: string;
  description: string;
  difficultyLevel: string;
  orderIndex: number;
  estimatedDurationMinutes: number;
  thumbnailUrl?: string;
  isActive: boolean;
  sessionCount: number;
  sessions: TopicSession[];
}

const DIFFICULTY_LABELS: Record<string, string> = {
  BEGINNER: 'Cơ bản',
  INTERMEDIATE: 'Trung cấp',
  ADVANCED: 'Nâng cao',
};

const DIFFICULTY_COLORS: Record<string, string> = {
  BEGINNER: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
  INTERMEDIATE: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
  ADVANCED: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
};

const TOPIC_GRADIENTS = [
  'from-blue-500 to-purple-600',
  'from-green-500 to-teal-600',
  'from-orange-500 to-red-500',
  'from-pink-500 to-rose-600',
  'from-indigo-500 to-blue-600',
  'from-teal-500 to-cyan-600',
];

export function SessionOverview() {
  const [topics, setTopics] = useState<Topic[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selectedTopic, setSelectedTopic] = useState<Topic | null>(null);
  const [completedSessionIds, setCompletedSessionIds] = useState<Set<number>>(new Set());
  const navigate = useNavigate();
  const progressVersion = useAppStore((s) => s.progressVersion);

  useEffect(() => {
    apiService.student
      .getCompletedSessionIds()
      .then((res) => {
        const list = res.data?.data ?? [];
        setCompletedSessionIds(new Set(Array.isArray(list) ? list : []));
      })
      .catch(() => setCompletedSessionIds(new Set()));
  }, [progressVersion]);

  useEffect(() => {
    const parseList = (raw: any): Topic[] => {
      if (Array.isArray(raw)) return raw;
      if (Array.isArray(raw?.data)) return raw.data;
      return [];
    };

    apiService.topic
      .getActive()
      .then(async (res) => {
        let list = parseList(res.data);
        // Nếu active trả rỗng, thử lấy tất cả (fallback cho backend cũ)
        if (list.length === 0) {
          const res2 = await apiService.topic.getAll();
          list = parseList(res2.data);
        }
        setTopics(list);
      })
      .catch((err) => {
        const msg = err?.response?.data?.message ?? err?.message ?? 'Lỗi kết nối';
        toast.error('Không tải được chủ đề', { description: msg });
        setTopics([]);
      })
      .finally(() => setLoading(false));
  }, []);

  const filteredTopics = topics.filter(
    (t) =>
      t.name.toLowerCase().includes(search.toLowerCase()) ||
      (t.description ?? '').toLowerCase().includes(search.toLowerCase())
  );

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <p className="text-gray-500">Đang tải chủ đề học...</p>
      </div>
    );
  }

  // --- Session list view for a specific topic ---
  if (selectedTopic) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-3xl">
        <button
          onClick={() => setSelectedTopic(null)}
          className="flex items-center gap-1 text-sm text-blue-600 hover:underline mb-6"
        >
          ← Quay lại danh sách chủ đề
        </button>

        <div className="mb-6">
          <div className="flex items-center gap-3 mb-2">
            <div
              className={`w-12 h-12 rounded-xl bg-gradient-to-br ${TOPIC_GRADIENTS[selectedTopic.orderIndex % TOPIC_GRADIENTS.length]} flex items-center justify-center`}
            >
              <BookOpen className="h-6 w-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold">{selectedTopic.name}</h1>
              {selectedTopic.difficultyLevel && (
                <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${DIFFICULTY_COLORS[selectedTopic.difficultyLevel]}`}>
                  {DIFFICULTY_LABELS[selectedTopic.difficultyLevel] ?? selectedTopic.difficultyLevel}
                </span>
              )}
            </div>
          </div>
          {selectedTopic.description && (
            <p className="text-gray-600 dark:text-gray-400 text-sm">{selectedTopic.description}</p>
          )}
          <div className="flex gap-4 mt-3 text-sm text-gray-500">
            <span className="flex items-center gap-1">
              <Layers className="h-4 w-4" />
              {selectedTopic.sessionCount} session
            </span>
            {selectedTopic.estimatedDurationMinutes && (
              <span className="flex items-center gap-1">
                <Clock className="h-4 w-4" />
                ~{selectedTopic.estimatedDurationMinutes} phút
              </span>
            )}
          </div>
        </div>

        {(selectedTopic.sessions ?? []).length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            <Layers className="mx-auto h-10 w-10 mb-3 text-gray-300" />
            <p>Chủ đề này chưa có bài học nào.</p>
          </div>
        ) : (
          <div className="space-y-3">
            {(selectedTopic.sessions ?? []).map((session, idx) => {
              const isCompleted = completedSessionIds.has(session.id);
              return (
              <button
                key={session.id}
                onClick={() => navigate(`/sessions/${session.id}`)}
                className="w-full flex items-center gap-4 bg-white dark:bg-gray-800 rounded-xl p-4 border hover:border-blue-400 hover:shadow-md transition-all text-left group"
              >
                <div className="w-10 h-10 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 flex items-center justify-center text-sm font-bold flex-shrink-0">
                  {isCompleted ? <CheckCircle className="h-5 w-5 text-green-600" /> : idx + 1}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-900 dark:text-white group-hover:text-blue-600 transition-colors">
                    {session.title}
                  </p>
                  <p className="text-sm text-gray-500">
                    {session.lessonCount} bài học {isCompleted && '· Đã hoàn thành'}
                  </p>
                </div>
                <ChevronRight className="h-5 w-5 text-gray-400 group-hover:text-blue-500 flex-shrink-0" />
              </button>
              );
            })}
          </div>
        )}
      </div>
    );
  }

  // --- Topic list view ---
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="mb-2 text-3xl font-bold">Chủ đề học</h1>
        <p className="text-gray-600 dark:text-gray-400">
          Chọn một chủ đề để bắt đầu luyện tập tiếng Anh
        </p>
      </div>

      {/* Search */}
      <div className="relative mb-6 max-w-md">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
        <Input
          placeholder="Tìm kiếm chủ đề..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="pl-9"
        />
      </div>

      {filteredTopics.length === 0 ? (
        <div className="text-center py-16">
          <BookOpen className="mx-auto h-14 w-14 text-gray-300 mb-4" />
          <p className="text-gray-500 text-lg">Chưa có chủ đề nào.</p>
          <p className="text-gray-400 text-sm">Admin đang cập nhật nội dung, vui lòng quay lại sau.</p>
        </div>
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {filteredTopics.map((topic, idx) => (
            <button
              key={topic.id}
              onClick={() => setSelectedTopic(topic)}
              className="group bg-white dark:bg-gray-800 rounded-2xl border hover:border-blue-400 hover:shadow-lg transition-all text-left overflow-hidden"
            >
              {/* Gradient header */}
              <div className={`h-24 bg-gradient-to-br ${TOPIC_GRADIENTS[idx % TOPIC_GRADIENTS.length]} relative flex items-center justify-center`}>
                {topic.thumbnailUrl ? (
                  <img src={topic.thumbnailUrl} alt={topic.name} className="absolute inset-0 w-full h-full object-cover opacity-30" />
                ) : null}
                <BookOpen className="h-10 w-10 text-white opacity-80" />
                <div className="absolute top-2 left-2">
                  <span className="text-xs text-white/80 bg-white/20 px-2 py-0.5 rounded-full">
                    #{topic.orderIndex}
                  </span>
                </div>
                {topic.difficultyLevel && (
                  <div className="absolute top-2 right-2">
                    <Badge className={`text-xs border-0 ${DIFFICULTY_COLORS[topic.difficultyLevel]}`}>
                      {DIFFICULTY_LABELS[topic.difficultyLevel] ?? topic.difficultyLevel}
                    </Badge>
                  </div>
                )}
              </div>

              {/* Content */}
              <div className="p-4">
                <h3 className="font-bold text-gray-900 dark:text-white group-hover:text-blue-600 transition-colors text-lg leading-tight">
                  {topic.name}
                </h3>
                {topic.description && (
                  <p className="text-sm text-gray-500 mt-1 line-clamp-2">{topic.description}</p>
                )}
                <div className="flex items-center gap-4 mt-3 text-xs text-gray-400">
                  <span className="flex items-center gap-1">
                    <Layers className="h-3 w-3" />
                    {topic.sessionCount} session
                  </span>
                  {topic.estimatedDurationMinutes && (
                    <span className="flex items-center gap-1">
                      <Clock className="h-3 w-3" />
                      ~{topic.estimatedDurationMinutes} phút
                    </span>
                  )}
                </div>
                <div className="mt-3 flex items-center text-blue-600 text-sm font-medium group-hover:gap-2 transition-all">
                  Xem các bài học
                  <ChevronRight className="h-4 w-4 ml-1" />
                </div>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

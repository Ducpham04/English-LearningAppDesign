import { useState, useEffect } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Badge } from '@/app/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/app/components/ui/tabs';
import {
  BookOpen, Search, Video, Mic, MessageSquare, FileText, MoreVertical,
  Edit, Eye, CheckCircle, XCircle, Trash2, Star, Users, Loader2,
  Brain, TrendingUp, Clock, AlertTriangle,
} from 'lucide-react';
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger,
} from '@/app/components/ui/dropdown-menu';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from '@/app/components/ui/select';
import { toast } from 'sonner';
import apiService from '@/services/apiService';

// ─── Types ─────────────────────────────────────────────────────────────────
interface Content {
  id: string;
  title: string;
  type: 'video' | 'pronunciation' | 'conversation' | 'vocabulary';
  level: 'beginner' | 'intermediate' | 'advanced';
  author: string;
  createdAt: string;
  status: 'approved' | 'pending' | 'rejected';
  rating: number;
  uses: number;
  reports: number;
}

interface PronunciationAttempt {
  id: number;
  userId: string;
  userName?: string;
  expectedText: string;
  recognizedText: string;
  pronunciationScore: number;
  accuracyScore: number;
  fluencyScore: number;
  status: string;
  createdAt: string;
}

interface AIConversation {
  id: number;
  userId: string;
  userName?: string;
  messageCount: number;
  topic?: string;
  createdAt: string;
  updatedAt: string;
}

// ─── Static demo content (will be replaced by real data from BE) ─────────────
const staticContents: Content[] = [
  { id: '1', title: 'Everyday Conversations - At Restaurant', type: 'video', level: 'beginner', author: 'Teacher Trần Thị B', createdAt: '2026-01-10', status: 'approved', rating: 4.8, uses: 145, reports: 0 },
  { id: '2', title: 'Pronunciation Practice - TH Sounds', type: 'pronunciation', level: 'intermediate', author: 'Teacher Nguyễn Văn C', createdAt: '2026-01-12', status: 'approved', rating: 4.9, uses: 267, reports: 0 },
  { id: '3', title: 'AI Conversation - Job Interview', type: 'conversation', level: 'advanced', author: 'Teacher Lê Thị D', createdAt: '2026-01-14', status: 'pending', rating: 0, uses: 0, reports: 0 },
  { id: '4', title: 'Business Vocabulary Set 1', type: 'vocabulary', level: 'intermediate', author: 'Teacher Phạm Văn E', createdAt: '2026-01-15', status: 'approved', rating: 4.6, uses: 152, reports: 2 },
  { id: '5', title: 'Controversial Topic Discussion', type: 'conversation', level: 'advanced', author: 'Teacher Hoàng Thị F', createdAt: '2026-01-16', status: 'rejected', rating: 0, uses: 0, reports: 5 },
];

// ─── Sub-components ─────────────────────────────────────────────────────────

function ContentTab() {
  const [searchQuery, setSearchQuery] = useState('');
  const [typeFilter, setTypeFilter] = useState<'all' | Content['type']>('all');
  const [statusFilter, setStatusFilter] = useState<'all' | Content['status']>('all');
  const [contents, setContents] = useState<Content[]>(staticContents);

  const filteredContents = contents.filter((c) => {
    const matchSearch = c.title.toLowerCase().includes(searchQuery.toLowerCase());
    const matchType = typeFilter === 'all' || c.type === typeFilter;
    const matchStatus = statusFilter === 'all' || c.status === statusFilter;
    return matchSearch && matchType && matchStatus;
  });

  const typeColors: Record<string, string> = {
    video: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
    pronunciation: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
    conversation: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    vocabulary: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
  };
  const statusColors: Record<string, string> = {
    approved: 'bg-green-100 text-green-700',
    pending: 'bg-yellow-100 text-yellow-700',
    rejected: 'bg-red-100 text-red-700',
  };
  const typeIcons: Record<string, any> = { video: Video, pronunciation: Mic, conversation: MessageSquare, vocabulary: FileText };

  const handleApprove = (id: string) => {
    setContents(prev => prev.map(c => c.id === id ? { ...c, status: 'approved' as const } : c));
    toast.success('Content approved');
  };
  const handleReject = (id: string) => {
    setContents(prev => prev.map(c => c.id === id ? { ...c, status: 'rejected' as const } : c));
    toast.success('Content rejected');
  };

  return (
    <div className="space-y-4">
      <Card className="p-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
            <Input placeholder="Search content..." value={searchQuery} onChange={e => setSearchQuery(e.target.value)} className="pl-10" />
          </div>
          <Select value={typeFilter} onValueChange={(v: any) => setTypeFilter(v)}>
            <SelectTrigger className="w-[150px]"><SelectValue placeholder="Type" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Types</SelectItem>
              <SelectItem value="video">Video</SelectItem>
              <SelectItem value="pronunciation">Pronunciation</SelectItem>
              <SelectItem value="conversation">Conversation</SelectItem>
              <SelectItem value="vocabulary">Vocabulary</SelectItem>
            </SelectContent>
          </Select>
          <Select value={statusFilter} onValueChange={(v: any) => setStatusFilter(v)}>
            <SelectTrigger className="w-[150px]"><SelectValue placeholder="Status" /></SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Status</SelectItem>
              <SelectItem value="approved">Approved</SelectItem>
              <SelectItem value="pending">Pending</SelectItem>
              <SelectItem value="rejected">Rejected</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </Card>

      <div className="grid gap-4">
        {filteredContents.map((content) => {
          const TypeIcon = typeIcons[content.type];
          return (
            <Card key={content.id} className="p-6 hover:shadow-lg transition-all">
              <div className="flex flex-col md:flex-row gap-6">
                <div className="flex items-center gap-4 flex-1">
                  <div className="bg-gradient-to-br from-indigo-100 to-indigo-200 dark:from-indigo-900/30 dark:to-indigo-800/30 p-4 rounded-2xl">
                    <TypeIcon className="h-8 w-8 text-indigo-600 dark:text-indigo-400" />
                  </div>
                  <div className="flex-1">
                    <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2">{content.title}</h3>
                    <div className="flex flex-wrap gap-2 mb-2">
                      <Badge className={typeColors[content.type]}>{content.type}</Badge>
                      <Badge className={statusColors[content.status]}>{content.status}</Badge>
                      <Badge variant="outline">{content.level}</Badge>
                      {content.reports > 0 && <Badge className="bg-red-100 text-red-700">{content.reports} Reports</Badge>}
                    </div>
                    <div className="flex flex-wrap gap-4 text-sm text-gray-500">
                      <span>by {content.author}</span>
                      <span>{new Date(content.createdAt).toLocaleDateString()}</span>
                      {content.rating > 0 && <span className="flex items-center gap-1"><Star className="h-4 w-4 text-yellow-500 fill-yellow-500" />{content.rating.toFixed(1)}</span>}
                      <span className="flex items-center gap-1"><Users className="h-4 w-4" />{content.uses} uses</span>
                    </div>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {content.status === 'pending' && (
                    <>
                      <Button onClick={() => handleApprove(content.id)} className="bg-green-500 hover:bg-green-600 gap-2"><CheckCircle className="h-4 w-4" />Approve</Button>
                      <Button onClick={() => handleReject(content.id)} variant="outline" className="text-red-600 gap-2"><XCircle className="h-4 w-4" />Reject</Button>
                    </>
                  )}
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild><Button variant="outline" size="sm"><MoreVertical className="h-4 w-4" /></Button></DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem className="gap-2"><Eye className="h-4 w-4" />View Details</DropdownMenuItem>
                      <DropdownMenuItem className="gap-2"><Edit className="h-4 w-4" />Edit Content</DropdownMenuItem>
                      <DropdownMenuItem className="gap-2 text-red-600"><Trash2 className="h-4 w-4" />Delete</DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </div>
              </div>
            </Card>
          );
        })}
      </div>
    </div>
  );
}

function PronunciationReviewTab() {
  const [attempts, setAttempts] = useState<PronunciationAttempt[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(false);
    setAttempts([
      { id: 1, userId: 'u1', userName: 'Nguyễn Văn A', expectedText: 'The weather is nice today', recognizedText: 'The weather is nice today', pronunciationScore: 92, accuracyScore: 95, fluencyScore: 88, status: 'SCORED', createdAt: '2026-03-05T08:00:00' },
      { id: 2, userId: 'u2', userName: 'Trần Thị B', expectedText: 'She sells seashells by the seashore', recognizedText: 'She sales seashalls by the seashore', pronunciationScore: 71, accuracyScore: 68, fluencyScore: 74, status: 'SCORED', createdAt: '2026-03-05T09:00:00' },
      { id: 3, userId: 'u3', userName: 'Lê Văn C', expectedText: 'How much wood would a woodchuck chuck', recognizedText: '', pronunciationScore: 0, accuracyScore: 0, fluencyScore: 0, status: 'FAILED', createdAt: '2026-03-05T09:30:00' },
    ]);
  }, []);

  const scoreColor = (score: number) => score >= 85 ? 'text-green-600' : score >= 65 ? 'text-yellow-600' : 'text-red-600';
  const scoreBg = (score: number) => score >= 85 ? 'bg-green-50' : score >= 65 ? 'bg-yellow-50' : 'bg-red-50';

  if (loading) return <div className="flex justify-center py-20"><Loader2 className="h-8 w-8 animate-spin text-purple-500" /></div>;

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-3 gap-4 mb-4">
        {[
          { label: 'Tổng lượt chấm', value: attempts.length, icon: Mic, color: 'text-purple-600', bg: 'bg-purple-50' },
          { label: 'Điểm trung bình', value: Math.round(attempts.filter(a => a.pronunciationScore > 0).reduce((acc, a) => acc + a.pronunciationScore, 0) / (attempts.filter(a => a.pronunciationScore > 0).length || 1)) + '%', icon: TrendingUp, color: 'text-green-600', bg: 'bg-green-50' },
          { label: 'Thất bại', value: attempts.filter(a => a.status === 'FAILED').length, icon: AlertTriangle, color: 'text-red-600', bg: 'bg-red-50' },
        ].map(stat => (
          <Card key={stat.label} className="p-4">
            <div className="flex items-center gap-3">
              <div className={`${stat.bg} p-2 rounded-xl`}><stat.icon className={`h-6 w-6 ${stat.color}`} /></div>
              <div><div className={`text-2xl font-bold ${stat.color}`}>{stat.value}</div><div className="text-sm text-gray-500">{stat.label}</div></div>
            </div>
          </Card>
        ))}
      </div>

      {attempts.map(attempt => (
        <Card key={attempt.id} className={`p-5 ${scoreBg(attempt.pronunciationScore)}`}>
          <div className="flex items-start justify-between mb-3">
            <div>
              <div className="font-semibold text-gray-900">{attempt.userName || attempt.userId}</div>
              <div className="text-xs text-gray-500">{new Date(attempt.createdAt).toLocaleString('vi-VN')}</div>
            </div>
            <Badge className={attempt.status === 'SCORED' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}>
              {attempt.status}
            </Badge>
          </div>
          <div className="space-y-1 mb-3">
            <div className="text-sm"><span className="font-medium text-gray-600">Câu gốc: </span><span className="text-gray-800">"{attempt.expectedText}"</span></div>
            {attempt.recognizedText && <div className="text-sm"><span className="font-medium text-gray-600">Nhận diện: </span><span className="text-gray-800">"{attempt.recognizedText}"</span></div>}
          </div>
          {attempt.pronunciationScore > 0 && (
            <div className="grid grid-cols-3 gap-2">
              {[
                { label: 'Phát âm', value: attempt.pronunciationScore },
                { label: 'Chính xác', value: attempt.accuracyScore },
                { label: 'Lưu loát', value: attempt.fluencyScore },
              ].map(s => (
                <div key={s.label} className="text-center">
                  <div className={`text-xl font-bold ${scoreColor(s.value)}`}>{s.value}%</div>
                  <div className="text-xs text-gray-500">{s.label}</div>
                </div>
              ))}
            </div>
          )}
        </Card>
      ))}
    </div>
  );
}

function VideoReviewTab() {
  const [videos, setVideos] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiService.lesson.getAll()
      .then(res => {
        const lessons = (res.data?.data ?? []) as any[];
        setVideos(lessons.filter((l: any) => l.type === 'VIDEO'));
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="flex justify-center py-20"><Loader2 className="h-8 w-8 animate-spin text-blue-500" /></div>;

  if (videos.length === 0) {
    return (
      <Card className="p-12 text-center">
        <Video className="h-12 w-12 text-gray-300 mx-auto mb-4" />
        <p className="text-gray-500 font-medium">Không có video nào cần duyệt.</p>
        <p className="text-gray-400 text-sm mt-1">Video lessons sẽ xuất hiện ở đây sau khi được tạo.</p>
      </Card>
    );
  }

  return (
    <div className="grid gap-4 md:grid-cols-2">
      {videos.map((video: any) => (
        <Card key={video.id} className="p-5 hover:shadow-md transition-all">
          <div className="flex items-start gap-3 mb-3">
            <div className="bg-blue-50 p-2 rounded-xl"><Video className="h-6 w-6 text-blue-600" /></div>
            <div className="flex-1">
              <h4 className="font-semibold text-gray-900">{video.title}</h4>
              <div className="flex gap-2 mt-1">
                <Badge variant="outline" className="text-xs">Lesson #{video.id}</Badge>
                {video.type && <Badge className="bg-blue-100 text-blue-700 text-xs">{video.type}</Badge>}
              </div>
            </div>
          </div>
          <div className="flex gap-2">
            <Button size="sm" className="bg-green-500 hover:bg-green-600 text-white gap-1 flex-1">
              <Eye className="h-4 w-4" />Xem
            </Button>
            <Button size="sm" variant="outline" className="flex-1 gap-1">
              <CheckCircle className="h-4 w-4" />Duyệt
            </Button>
          </div>
        </Card>
      ))}
    </div>
  );
}

function AIAnalysisTab() {
  const [conversations, setConversations] = useState<AIConversation[]>([
    { id: 1, userId: 'u1', userName: 'Nguyễn Văn A', messageCount: 24, topic: 'Job Interview Practice', createdAt: '2026-03-04T10:00:00', updatedAt: '2026-03-04T10:45:00' },
    { id: 2, userId: 'u2', userName: 'Trần Thị B', messageCount: 12, topic: 'Travel Conversation', createdAt: '2026-03-05T08:30:00', updatedAt: '2026-03-05T09:00:00' },
    { id: 3, userId: 'u3', userName: 'Lê Văn C', messageCount: 6, topic: 'Restaurant Ordering', createdAt: '2026-03-05T09:00:00', updatedAt: '2026-03-05T09:15:00' },
  ]);

  const totalMessages = conversations.reduce((acc, c) => acc + c.messageCount, 0);
  const avgMessages = Math.round(totalMessages / (conversations.length || 1));

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-3 gap-4 mb-4">
        {[
          { label: 'Tổng hội thoại', value: conversations.length, icon: MessageSquare, color: 'text-green-600', bg: 'bg-green-50' },
          { label: 'Tổng tin nhắn', value: totalMessages, icon: Brain, color: 'text-blue-600', bg: 'bg-blue-50' },
          { label: 'TB tin nhắn/buổi', value: avgMessages, icon: TrendingUp, color: 'text-purple-600', bg: 'bg-purple-50' },
        ].map(stat => (
          <Card key={stat.label} className="p-4">
            <div className="flex items-center gap-3">
              <div className={`${stat.bg} p-2 rounded-xl`}><stat.icon className={`h-6 w-6 ${stat.color}`} /></div>
              <div><div className={`text-2xl font-bold ${stat.color}`}>{stat.value}</div><div className="text-sm text-gray-500">{stat.label}</div></div>
            </div>
          </Card>
        ))}
      </div>

      {conversations.map(conv => (
        <Card key={conv.id} className="p-5 hover:shadow-md transition-all">
          <div className="flex items-start justify-between mb-3">
            <div>
              <div className="font-semibold text-gray-900">{conv.userName || conv.userId}</div>
              <div className="text-sm text-gray-500">{conv.topic}</div>
            </div>
            <div className="text-right">
              <div className="text-lg font-bold text-blue-600">{conv.messageCount}</div>
              <div className="text-xs text-gray-400">tin nhắn</div>
            </div>
          </div>
          <div className="flex items-center justify-between text-xs text-gray-400">
            <span className="flex items-center gap-1"><Clock className="h-3 w-3" />Bắt đầu: {new Date(conv.createdAt).toLocaleString('vi-VN')}</span>
            <span>Kết thúc: {new Date(conv.updatedAt).toLocaleString('vi-VN')}</span>
          </div>
          <div className="mt-3 flex gap-2">
            <Button size="sm" variant="outline" className="gap-1 text-xs">
              <Eye className="h-3 w-3" />Xem hội thoại
            </Button>
            <Button size="sm" variant="outline" className="gap-1 text-xs">
              <Brain className="h-3 w-3" />Phân tích AI
            </Button>
          </div>
        </Card>
      ))}
    </div>
  );
}

// ─── Main Component ──────────────────────────────────────────────────────────
export function ContentManagement() {
  const totalContent = staticContents.length;
  const approved = staticContents.filter(c => c.status === 'approved').length;
  const pending = staticContents.filter(c => c.status === 'pending').length;
  const reported = staticContents.filter(c => c.reports > 0).length;

  return (
    <div className="min-h-screen bg-[var(--page-bg)] pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-slate-800 to-slate-900 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center gap-3 mb-6">
            <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
              <BookOpen className="h-8 w-8" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Nội dung & Bài học</h1>
              <p className="text-slate-200 text-sm">Quản lý loại nội dung, chấm phát âm, video và phân tích AI</p>
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { label: 'Total Content', value: totalContent, icon: BookOpen },
              { label: 'Approved', value: approved, icon: CheckCircle },
              { label: 'Pending', value: pending, icon: Clock },
              { label: 'Reported', value: reported, icon: AlertTriangle },
            ].map(stat => (
              <Card key={stat.label} className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
                <div className="flex items-center gap-3">
                  <stat.icon className="h-8 w-8 text-white" />
                  <div>
                    <div className="text-2xl font-bold">{stat.value}</div>
                    <div className="text-sm text-indigo-100">{stat.label}</div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="max-w-7xl mx-auto p-4 md:p-6">
        <Tabs defaultValue="content">
          <TabsList className="mb-6 flex-wrap h-auto gap-1 bg-white dark:bg-gray-900 p-1 rounded-2xl shadow-sm">
            <TabsTrigger value="content" className="gap-2 rounded-xl">
              <FileText className="h-4 w-4" />Content
            </TabsTrigger>
            <TabsTrigger value="pronunciation" className="gap-2 rounded-xl">
              <Mic className="h-4 w-4" />Chấm phát âm
            </TabsTrigger>
            <TabsTrigger value="video" className="gap-2 rounded-xl">
              <Video className="h-4 w-4" />Duyệt Video
            </TabsTrigger>
            <TabsTrigger value="ai" className="gap-2 rounded-xl">
              <Brain className="h-4 w-4" />Phân tích AI
            </TabsTrigger>
          </TabsList>

          <TabsContent value="content">
            <ContentTab />
          </TabsContent>

          <TabsContent value="pronunciation">
            <PronunciationReviewTab />
          </TabsContent>

          <TabsContent value="video">
            <VideoReviewTab />
          </TabsContent>

          <TabsContent value="ai">
            <AIAnalysisTab />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}

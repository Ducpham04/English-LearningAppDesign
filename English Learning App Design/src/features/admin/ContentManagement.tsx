import { useState } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Badge } from '@/app/components/ui/badge';
import {
  BookOpen,
  Search,
  Video,
  Mic,
  MessageSquare,
  FileText,
  Plus,
  MoreVertical,
  Edit,
  Eye,
  CheckCircle,
  XCircle,
  Trash2,
  Star,
  Users,
} from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/app/components/ui/dropdown-menu';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/app/components/ui/select';

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

export function ContentManagement() {
  const [searchQuery, setSearchQuery] = useState('');
  const [typeFilter, setTypeFilter] = useState<'all' | Content['type']>('all');
  const [statusFilter, setStatusFilter] = useState<'all' | Content['status']>('all');

  const contents: Content[] = [
    {
      id: '1',
      title: 'Everyday Conversations - At Restaurant',
      type: 'video',
      level: 'beginner',
      author: 'Teacher Trần Thị B',
      createdAt: '2026-01-10',
      status: 'approved',
      rating: 4.8,
      uses: 145,
      reports: 0,
    },
    {
      id: '2',
      title: 'Pronunciation Practice - TH Sounds',
      type: 'pronunciation',
      level: 'intermediate',
      author: 'Teacher Nguyễn Văn C',
      createdAt: '2026-01-12',
      status: 'approved',
      rating: 4.9,
      uses: 267,
      reports: 0,
    },
    {
      id: '3',
      title: 'AI Conversation - Job Interview',
      type: 'conversation',
      level: 'advanced',
      author: 'Teacher Lê Thị D',
      createdAt: '2026-01-14',
      status: 'pending',
      rating: 0,
      uses: 0,
      reports: 0,
    },
    {
      id: '4',
      title: 'Business Vocabulary Set 1',
      type: 'vocabulary',
      level: 'intermediate',
      author: 'Teacher Phạm Văn E',
      createdAt: '2026-01-15',
      status: 'approved',
      rating: 4.6,
      uses: 152,
      reports: 2,
    },
    {
      id: '5',
      title: 'Controversial Topic Discussion',
      type: 'conversation',
      level: 'advanced',
      author: 'Teacher Hoàng Thị F',
      createdAt: '2026-01-16',
      status: 'rejected',
      rating: 0,
      uses: 0,
      reports: 5,
    },
  ];

  const filteredContents = contents.filter((content) => {
    const matchesSearch = content.title.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesType = typeFilter === 'all' || content.type === typeFilter;
    const matchesStatus = statusFilter === 'all' || content.status === statusFilter;
    return matchesSearch && matchesType && matchesStatus;
  });

  const stats = {
    total: contents.length,
    approved: contents.filter((c) => c.status === 'approved').length,
    pending: contents.filter((c) => c.status === 'pending').length,
    reported: contents.filter((c) => c.reports > 0).length,
  };

  const typeColors = {
    video: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
    pronunciation: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
    conversation: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    vocabulary: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
  };

  const statusColors = {
    approved: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    pending: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
    rejected: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
  };

  const getTypeIcon = (type: Content['type']) => {
    switch (type) {
      case 'video': return Video;
      case 'pronunciation': return Mic;
      case 'conversation': return MessageSquare;
      case 'vocabulary': return FileText;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-indigo-400 to-indigo-600 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
                <BookOpen className="h-8 w-8" />
              </div>
              <div>
                <h1 className="text-3xl font-bold">Content Management</h1>
                <p className="text-indigo-50">Review and moderate platform content</p>
              </div>
            </div>
            <Button className="bg-white text-indigo-600 hover:bg-indigo-50 gap-2">
              <Plus className="h-5 w-5" />
              Create Content
            </Button>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <BookOpen className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.total}</div>
                  <div className="text-sm text-indigo-50">Total Content</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <CheckCircle className="h-8 w-8 text-green-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.approved}</div>
                  <div className="text-sm text-indigo-50">Approved</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <XCircle className="h-8 w-8 text-yellow-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.pending}</div>
                  <div className="text-sm text-indigo-50">Pending</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <XCircle className="h-8 w-8 text-red-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.reported}</div>
                  <div className="text-sm text-indigo-50">Reported</div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto p-4 md:p-6">
        {/* Filters */}
        <Card className="p-4 mb-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
              <Input
                placeholder="Search content..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={typeFilter} onValueChange={(value: any) => setTypeFilter(value)}>
              <SelectTrigger className="w-[150px]">
                <SelectValue placeholder="Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="video">Video</SelectItem>
                <SelectItem value="pronunciation">Pronunciation</SelectItem>
                <SelectItem value="conversation">Conversation</SelectItem>
                <SelectItem value="vocabulary">Vocabulary</SelectItem>
              </SelectContent>
            </Select>
            <Select value={statusFilter} onValueChange={(value: any) => setStatusFilter(value)}>
              <SelectTrigger className="w-[150px]">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Status</SelectItem>
                <SelectItem value="approved">Approved</SelectItem>
                <SelectItem value="pending">Pending</SelectItem>
                <SelectItem value="rejected">Rejected</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </Card>

        {/* Content List */}
        <div className="grid gap-4">
          {filteredContents.map((content) => {
            const TypeIcon = getTypeIcon(content.type);
            return (
              <Card key={content.id} className="p-6 hover:shadow-lg transition-all duration-300">
                <div className="flex flex-col md:flex-row gap-6">
                  {/* Icon & Info */}
                  <div className="flex items-center gap-4 flex-1">
                    <div className="bg-gradient-to-br from-indigo-100 to-indigo-200 dark:from-indigo-900/30 dark:to-indigo-800/30 p-4 rounded-2xl">
                      <TypeIcon className="h-8 w-8 text-indigo-600 dark:text-indigo-400" />
                    </div>
                    <div className="flex-1">
                      <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2">
                        {content.title}
                      </h3>
                      <div className="flex flex-wrap gap-2 mb-2">
                        <Badge className={typeColors[content.type]}>
                          {content.type.charAt(0).toUpperCase() + content.type.slice(1)}
                        </Badge>
                        <Badge className={statusColors[content.status]}>
                          {content.status.charAt(0).toUpperCase() + content.status.slice(1)}
                        </Badge>
                        <Badge variant="outline">{content.level}</Badge>
                        {content.reports > 0 && (
                          <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400">
                            {content.reports} Reports
                          </Badge>
                        )}
                      </div>
                      <div className="flex flex-wrap gap-4 text-sm text-gray-600 dark:text-gray-400">
                        <div>by {content.author}</div>
                        <div>{new Date(content.createdAt).toLocaleDateString()}</div>
                        {content.rating > 0 && (
                          <div className="flex items-center gap-1">
                            <Star className="h-4 w-4 text-yellow-500 fill-yellow-500" />
                            <span>{content.rating.toFixed(1)}</span>
                          </div>
                        )}
                        <div className="flex items-center gap-1">
                          <Users className="h-4 w-4" />
                          <span>{content.uses} uses</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex items-center gap-2">
                    {content.status === 'pending' && (
                      <>
                        <Button className="bg-green-500 hover:bg-green-600 gap-2">
                          <CheckCircle className="h-4 w-4" />
                          Approve
                        </Button>
                        <Button variant="outline" className="text-red-600 gap-2">
                          <XCircle className="h-4 w-4" />
                          Reject
                        </Button>
                      </>
                    )}
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="outline" size="sm">
                          <MoreVertical className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuItem className="gap-2">
                          <Eye className="h-4 w-4" />
                          View Details
                        </DropdownMenuItem>
                        <DropdownMenuItem className="gap-2">
                          <Edit className="h-4 w-4" />
                          Edit Content
                        </DropdownMenuItem>
                        {content.status === 'approved' && (
                          <DropdownMenuItem className="gap-2 text-orange-600">
                            <XCircle className="h-4 w-4" />
                            Unpublish
                          </DropdownMenuItem>
                        )}
                        <DropdownMenuItem className="gap-2 text-red-600">
                          <Trash2 className="h-4 w-4" />
                          Delete
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                </div>
              </Card>
            );
          })}
        </div>

        {filteredContents.length === 0 && (
          <Card className="p-12 text-center">
            <div className="flex flex-col items-center gap-4">
              <div className="bg-gray-100 dark:bg-gray-800 p-6 rounded-full">
                <BookOpen className="h-12 w-12 text-gray-400" />
              </div>
              <div>
                <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                  No content found
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  Try adjusting your filters
                </p>
              </div>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
}

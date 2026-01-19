import { useState } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/app/components/ui/tabs';
import { Badge } from '@/app/components/ui/badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/app/components/ui/select';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/app/components/ui/dialog';
import { Textarea } from '@/app/components/ui/textarea';
import { Label } from '@/app/components/ui/label';
import {
  ClipboardList,
  Search,
  CheckCircle2,
  Clock,
  AlertCircle,
  Eye,
  MessageSquare,
  TrendingUp,
  Award,
  FileText,
  Users,
  BookOpen,
} from 'lucide-react';
import { toast } from 'sonner';

interface Submission {
  id: string;
  studentId: string;
  studentName: string;
  assignmentId: string;
  assignmentTitle: string;
  type: 'test' | 'assignment' | 'quiz';
  status: 'pending' | 'graded' | 'returned';
  score?: number;
  maxScore: number;
  submittedAt: string;
  gradedAt?: string;
  feedback?: string;
  classId: string;
  className: string;
}

interface ClassGrading {
  id: string;
  name: string;
  code: string;
  pendingCount: number;
  totalStudents: number;
  recentSubmissions: Submission[];
}

export function GradingCenter() {
  const [selectedClass, setSelectedClass] = useState<string>('all');
  const [selectedTab, setSelectedTab] = useState<'pending' | 'graded' | 'all'>('pending');
  const [selectedType, setSelectedType] = useState<'all' | 'test' | 'assignment' | 'quiz'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedSubmission, setSelectedSubmission] = useState<Submission | null>(null);
  const [showGradeModal, setShowGradeModal] = useState(false);
  const [gradeData, setGradeData] = useState({ score: '', feedback: '' });

  // Mock data - Classes with pending assignments
  const classesWithPending: ClassGrading[] = [
    {
      id: '1',
      name: 'Beginner A1 - Morning',
      code: 'BEG1AM',
      pendingCount: 12,
      totalStudents: 25,
      recentSubmissions: [],
    },
    {
      id: '2',
      name: 'Intermediate B1 - Evening',
      code: 'INT1EV',
      pendingCount: 8,
      totalStudents: 20,
      recentSubmissions: [],
    },
    {
      id: '3',
      name: 'Advanced C1 - Weekend',
      code: 'ADV1WE',
      pendingCount: 5,
      totalStudents: 15,
      recentSubmissions: [],
    },
  ];

  // Mock data - Submissions
  const allSubmissions: Submission[] = [
    {
      id: '1',
      studentId: '1',
      studentName: 'John Smith',
      assignmentId: 'test-1',
      assignmentTitle: 'Week 1 Grammar Test',
      type: 'test',
      status: 'pending',
      maxScore: 100,
      submittedAt: '2026-01-15T10:30:00',
      classId: '1',
      className: 'Beginner A1 - Morning',
    },
    {
      id: '2',
      studentId: '2',
      studentName: 'Sarah Johnson',
      assignmentId: 'test-1',
      assignmentTitle: 'Week 1 Grammar Test',
      type: 'test',
      status: 'graded',
      score: 95,
      maxScore: 100,
      submittedAt: '2026-01-15T09:15:00',
      gradedAt: '2026-01-15T14:20:00',
      feedback: 'Excellent work! Very thorough understanding.',
      classId: '1',
      className: 'Beginner A1 - Morning',
    },
    {
      id: '3',
      studentId: '3',
      studentName: 'Mike Brown',
      assignmentId: 'assign-1',
      assignmentTitle: 'Pronunciation Practice Assignment',
      type: 'assignment',
      status: 'pending',
      maxScore: 50,
      submittedAt: '2026-01-16T08:45:00',
      classId: '2',
      className: 'Intermediate B1 - Evening',
    },
    {
      id: '4',
      studentId: '4',
      studentName: 'Emily Davis',
      assignmentId: 'quiz-1',
      assignmentTitle: 'Vocabulary Quiz - Unit 2',
      type: 'quiz',
      status: 'graded',
      score: 42,
      maxScore: 50,
      submittedAt: '2026-01-14T11:20:00',
      gradedAt: '2026-01-14T15:30:00',
      feedback: 'Good job! Review words 15-20.',
      classId: '2',
      className: 'Intermediate B1 - Evening',
    },
    {
      id: '5',
      studentId: '5',
      studentName: 'David Wilson',
      assignmentId: 'test-1',
      assignmentTitle: 'Week 1 Grammar Test',
      type: 'test',
      status: 'returned',
      score: 78,
      maxScore: 100,
      submittedAt: '2026-01-15T10:00:00',
      gradedAt: '2026-01-15T16:45:00',
      feedback: 'Review subject-verb agreement. Good effort overall.',
      classId: '3',
      className: 'Advanced C1 - Weekend',
    },
  ];

  const filteredSubmissions = allSubmissions.filter((sub) => {
    const matchesClass = selectedClass === 'all' || sub.classId === selectedClass;
    
    const matchesTab =
      selectedTab === 'all' ||
      (selectedTab === 'pending' && sub.status === 'pending') ||
      (selectedTab === 'graded' && (sub.status === 'graded' || sub.status === 'returned'));
    
    const matchesType = selectedType === 'all' || sub.type === selectedType;
    
    const matchesSearch =
      sub.studentName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      sub.assignmentTitle.toLowerCase().includes(searchQuery.toLowerCase()) ||
      sub.className.toLowerCase().includes(searchQuery.toLowerCase());
    
    return matchesClass && matchesTab && matchesType && matchesSearch;
  });

  const stats = {
    pending: allSubmissions.filter((s) => s.status === 'pending').length,
    graded: allSubmissions.filter((s) => s.status === 'graded').length,
    returned: allSubmissions.filter((s) => s.status === 'returned').length,
    avgScore:
      allSubmissions
        .filter((s) => s.score !== undefined)
        .reduce((sum, s) => sum + (s.score! / s.maxScore) * 100, 0) /
        (allSubmissions.filter((s) => s.score !== undefined).length || 1),
  };

  const handleGrade = (submission: Submission) => {
    setSelectedSubmission(submission);
    setGradeData({
      score: submission.score?.toString() || '',
      feedback: submission.feedback || '',
    });
    setShowGradeModal(true);
  };

  const handleSubmitGrade = () => {
    if (!selectedSubmission) return;

    const score = parseFloat(gradeData.score);
    if (isNaN(score) || score < 0 || score > selectedSubmission.maxScore) {
      toast.error(`Score must be between 0 and ${selectedSubmission.maxScore}`);
      return;
    }

    // Mock: Update submission
    toast.success('Grade submitted successfully! 🎉');
    setShowGradeModal(false);
    setSelectedSubmission(null);
    setGradeData({ score: '', feedback: '' });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'pending':
        return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400';
      case 'graded':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      case 'returned':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'test':
        return ClipboardList;
      case 'assignment':
        return FileText;
      case 'quiz':
        return Award;
      default:
        return FileText;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-purple-500 to-purple-700 text-white p-6 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center gap-3 mb-6">
            <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
              <ClipboardList className="h-8 w-8" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Grading Center</h1>
              <p className="text-purple-50">Review and grade student submissions</p>
            </div>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Clock className="h-8 w-8 text-yellow-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.pending}</div>
                  <div className="text-sm text-purple-50">Pending</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <CheckCircle2 className="h-8 w-8 text-green-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.graded}</div>
                  <div className="text-sm text-purple-50">Graded</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Award className="h-8 w-8 text-blue-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.returned}</div>
                  <div className="text-sm text-purple-50">Returned</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <TrendingUp className="h-8 w-8 text-purple-400" />
                <div>
                  <div className="text-2xl font-bold">{stats.avgScore.toFixed(1)}%</div>
                  <div className="text-sm text-purple-50">Avg Score</div>
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
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
              <Input
                placeholder="Search by student, assignment, or class..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={selectedType} onValueChange={(v: any) => setSelectedType(v)}>
              <SelectTrigger className="w-full md:w-48">
                <SelectValue placeholder="Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="test">Tests</SelectItem>
                <SelectItem value="assignment">Assignments</SelectItem>
                <SelectItem value="quiz">Quizzes</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </Card>

        {/* Tabs */}
        <Tabs value={selectedTab} onValueChange={(v: any) => setSelectedTab(v)}>
          <TabsList className="mb-6">
            <TabsTrigger value="pending" className="gap-2">
              <Clock className="h-4 w-4" />
              Pending ({stats.pending})
            </TabsTrigger>
            <TabsTrigger value="graded" className="gap-2">
              <CheckCircle2 className="h-4 w-4" />
              Graded ({stats.graded + stats.returned})
            </TabsTrigger>
            <TabsTrigger value="all" className="gap-2">
              <FileText className="h-4 w-4" />
              All
            </TabsTrigger>
          </TabsList>

          <TabsContent value={selectedTab} className="space-y-4">
            {filteredSubmissions.length === 0 ? (
              <Card className="p-12 text-center">
                <div className="flex flex-col items-center gap-4">
                  <div className="bg-gray-100 dark:bg-gray-800 p-6 rounded-full">
                    <ClipboardList className="h-12 w-12 text-gray-400" />
                  </div>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                      No submissions found
                    </h3>
                    <p className="text-gray-600 dark:text-gray-400">
                      {selectedTab === 'pending'
                        ? 'All caught up! No pending submissions to grade.'
                        : 'Try adjusting your filters.'}
                    </p>
                  </div>
                </div>
              </Card>
            ) : (
              filteredSubmissions.map((submission) => {
                const TypeIcon = getTypeIcon(submission.type);
                const timeSinceSubmit = Math.floor(
                  (Date.now() - new Date(submission.submittedAt).getTime()) / (1000 * 60 * 60)
                );

                return (
                  <Card
                    key={submission.id}
                    className="p-4 hover:shadow-lg transition-all duration-300"
                  >
                    <div className="flex flex-col md:flex-row md:items-center gap-4">
                      {/* Icon & Info */}
                      <div className="flex items-start gap-4 flex-1">
                        <div className="bg-purple-100 dark:bg-purple-900/30 p-3 rounded-xl">
                          <TypeIcon className="h-6 w-6 text-purple-600 dark:text-purple-400" />
                        </div>
                        <div className="flex-1">
                          <h3 className="font-bold text-gray-900 dark:text-white mb-1">
                            {submission.assignmentTitle}
                          </h3>
                          <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
                            Student: {submission.studentName}
                          </p>
                          <div className="flex flex-wrap gap-2">
                            <Badge className={getStatusColor(submission.status)}>
                              {submission.status.charAt(0).toUpperCase() +
                                submission.status.slice(1)}
                            </Badge>
                            <Badge variant="outline" className="capitalize">
                              {submission.type}
                            </Badge>
                            <Badge variant="outline">
                              <Clock className="h-3 w-3 mr-1" />
                              {timeSinceSubmit}h ago
                            </Badge>
                          </div>
                        </div>
                      </div>

                      {/* Score & Actions */}
                      <div className="flex items-center gap-4">
                        {submission.score !== undefined ? (
                          <div className="text-center">
                            <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">
                              {submission.score}
                              <span className="text-sm text-gray-500">
                                /{submission.maxScore}
                              </span>
                            </div>
                            <div className="text-xs text-gray-500">
                              {Math.round((submission.score / submission.maxScore) * 100)}%
                            </div>
                          </div>
                        ) : (
                          <div className="text-center px-4">
                            <AlertCircle className="h-8 w-8 text-yellow-500 mx-auto" />
                            <div className="text-xs text-gray-500 mt-1">Not Graded</div>
                          </div>
                        )}

                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            variant="outline"
                            className="gap-2"
                            onClick={() => {
                              toast.info('Opening submission details...');
                            }}
                          >
                            <Eye className="h-4 w-4" />
                            View
                          </Button>
                          <Button
                            size="sm"
                            className="gap-2 bg-purple-600 hover:bg-purple-700"
                            onClick={() => handleGrade(submission)}
                          >
                            <MessageSquare className="h-4 w-4" />
                            {submission.status === 'pending' ? 'Grade' : 'Edit'}
                          </Button>
                        </div>
                      </div>
                    </div>

                    {submission.feedback && (
                      <div className="mt-4 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                        <p className="text-sm text-gray-600 dark:text-gray-400">
                          <strong>Feedback:</strong> {submission.feedback}
                        </p>
                      </div>
                    )}
                  </Card>
                );
              })
            )}
          </TabsContent>
        </Tabs>
      </div>

      {/* Grading Modal */}
      <Dialog open={showGradeModal} onOpenChange={setShowGradeModal}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Grade Submission</DialogTitle>
            <DialogDescription>
              Review and grade this student's submission.
            </DialogDescription>
          </DialogHeader>

          {selectedSubmission && (
            <div className="space-y-6">
              {/* Submission Info */}
              <div className="bg-gray-50 dark:bg-gray-800 rounded-lg p-4">
                <h4 className="font-semibold mb-2">{selectedSubmission.assignmentTitle}</h4>
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  Student: {selectedSubmission.studentName}
                </p>
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  Submitted: {new Date(selectedSubmission.submittedAt).toLocaleString()}
                </p>
              </div>

              {/* Score Input */}
              <div className="space-y-2">
                <Label htmlFor="score">
                  Score * (Max: {selectedSubmission.maxScore})
                </Label>
                <Input
                  id="score"
                  type="number"
                  min="0"
                  max={selectedSubmission.maxScore}
                  value={gradeData.score}
                  onChange={(e) => setGradeData({ ...gradeData, score: e.target.value })}
                  placeholder="Enter score..."
                />
              </div>

              {/* Feedback */}
              <div className="space-y-2">
                <Label htmlFor="feedback">Feedback</Label>
                <Textarea
                  id="feedback"
                  value={gradeData.feedback}
                  onChange={(e) => setGradeData({ ...gradeData, feedback: e.target.value })}
                  placeholder="Provide feedback to the student..."
                  rows={5}
                />
              </div>

              {/* Quick Feedback Templates */}
              <div className="space-y-2">
                <Label className="text-sm text-gray-600">Quick Feedback:</Label>
                <div className="flex flex-wrap gap-2">
                  {[
                    'Excellent work!',
                    'Good job overall',
                    'Needs improvement',
                    'Review the material',
                    'Keep practicing',
                  ].map((template) => (
                    <Button
                      key={template}
                      type="button"
                      size="sm"
                      variant="outline"
                      onClick={() =>
                        setGradeData({
                          ...gradeData,
                          feedback: gradeData.feedback
                            ? `${gradeData.feedback} ${template}`
                            : template,
                        })
                      }
                    >
                      {template}
                    </Button>
                  ))}
                </div>
              </div>
            </div>
          )}

          <DialogFooter>
            <Button variant="outline" onClick={() => setShowGradeModal(false)}>
              Cancel
            </Button>
            <Button
              className="bg-purple-600 hover:bg-purple-700"
              onClick={handleSubmitGrade}
            >
              Submit Grade
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
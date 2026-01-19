import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Badge } from '@/app/components/ui/badge';
import { Progress } from '@/app/components/ui/progress';
import {
  FileText,
  Clock,
  CheckCircle2,
  XCircle,
  AlertCircle,
  Play,
  Trophy,
  Target,
  Calendar,
} from 'lucide-react';

interface Test {
  id: string;
  title: string;
  type: 'listening' | 'speaking' | 'vocabulary' | 'comprehensive';
  duration: number; // minutes
  questions: number;
  status: 'not-started' | 'in-progress' | 'completed';
  score?: number;
  maxScore: number;
  deadline?: string;
  attempts: number;
  maxAttempts: number;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
}

export function TestsScreen() {
  const [activeTab, setActiveTab] = useState<'available' | 'completed'>('available');
  const navigate = useNavigate();

  const tests: Test[] = [
    {
      id: '1',
      title: 'Mid-Term Listening Assessment',
      type: 'listening',
      duration: 30,
      questions: 20,
      status: 'not-started',
      maxScore: 100,
      deadline: '2026-01-25',
      attempts: 0,
      maxAttempts: 2,
      difficulty: 'intermediate',
    },
    {
      id: '2',
      title: 'Speaking Fluency Test',
      type: 'speaking',
      duration: 15,
      questions: 5,
      status: 'in-progress',
      maxScore: 100,
      attempts: 1,
      maxAttempts: 1,
      difficulty: 'advanced',
    },
    {
      id: '3',
      title: 'Vocabulary Quiz - Unit 3',
      type: 'vocabulary',
      duration: 20,
      questions: 30,
      status: 'completed',
      score: 85,
      maxScore: 100,
      attempts: 1,
      maxAttempts: 3,
      difficulty: 'beginner',
    },
    {
      id: '4',
      title: 'Comprehensive Final Exam',
      type: 'comprehensive',
      duration: 60,
      questions: 50,
      status: 'not-started',
      maxScore: 200,
      deadline: '2026-02-15',
      attempts: 0,
      maxAttempts: 1,
      difficulty: 'advanced',
    },
  ];

  const availableTests = tests.filter(t => t.status !== 'completed');
  const completedTests = tests.filter(t => t.status === 'completed');

  const typeColors = {
    listening: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
    speaking: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
    vocabulary: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    comprehensive: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
  };

  const difficultyColors = {
    beginner: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    intermediate: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
    advanced: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
  };

  const getStatusIcon = (status: Test['status']) => {
    switch (status) {
      case 'completed':
        return <CheckCircle2 className="h-5 w-5 text-green-500" />;
      case 'in-progress':
        return <AlertCircle className="h-5 w-5 text-yellow-500" />;
      default:
        return <FileText className="h-5 w-5 text-gray-400" />;
    }
  };

  const stats = {
    totalTests: tests.length,
    completed: completedTests.length,
    averageScore: completedTests.reduce((acc, t) => acc + (t.score || 0), 0) / (completedTests.length || 1),
    pending: availableTests.length,
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-400 to-green-600 text-white p-6 md:p-8">
        <div className="max-w-6xl mx-auto">
          <div className="flex items-center gap-3 mb-4">
            <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
              <FileText className="h-8 w-8" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Tests & Assessments</h1>
              <p className="text-green-50">Track your progress and achievements</p>
            </div>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <FileText className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.totalTests}</div>
                  <div className="text-sm text-green-50">Total Tests</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <CheckCircle2 className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.completed}</div>
                  <div className="text-sm text-green-50">Completed</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <Trophy className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.averageScore.toFixed(0)}%</div>
                  <div className="text-sm text-green-50">Avg Score</div>
                </div>
              </div>
            </Card>
            <Card className="bg-white/10 backdrop-blur-sm border-white/20 p-4">
              <div className="flex items-center gap-3">
                <AlertCircle className="h-8 w-8 text-white" />
                <div>
                  <div className="text-2xl font-bold">{stats.pending}</div>
                  <div className="text-sm text-green-50">Pending</div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-6xl mx-auto p-4 md:p-6">
        {/* Tabs */}
        <div className="flex gap-2 mb-6">
          <Button
            variant={activeTab === 'available' ? 'default' : 'outline'}
            onClick={() => setActiveTab('available')}
            className={activeTab === 'available' ? 'bg-green-500 hover:bg-green-600' : ''}
          >
            Available Tests ({availableTests.length})
          </Button>
          <Button
            variant={activeTab === 'completed' ? 'default' : 'outline'}
            onClick={() => setActiveTab('completed')}
            className={activeTab === 'completed' ? 'bg-green-500 hover:bg-green-600' : ''}
          >
            Completed ({completedTests.length})
          </Button>
        </div>

        {/* Tests List */}
        <div className="grid gap-4">
          {(activeTab === 'available' ? availableTests : completedTests).map((test) => (
            <Card key={test.id} className="p-6 hover:shadow-lg transition-all duration-300">
              <div className="flex flex-col md:flex-row md:items-center gap-4">
                {/* Icon & Status */}
                <div className="flex items-center gap-4 flex-1">
                  <div className="bg-gradient-to-br from-green-100 to-green-200 dark:from-green-900/30 dark:to-green-800/30 p-4 rounded-2xl">
                    {getStatusIcon(test.status)}
                  </div>
                  <div className="flex-1">
                    <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2">
                      {test.title}
                    </h3>
                    <div className="flex flex-wrap gap-2 mb-3">
                      <Badge className={typeColors[test.type]}>
                        {test.type.charAt(0).toUpperCase() + test.type.slice(1)}
                      </Badge>
                      <Badge className={difficultyColors[test.difficulty]}>
                        {test.difficulty.charAt(0).toUpperCase() + test.difficulty.slice(1)}
                      </Badge>
                      <Badge variant="outline" className="gap-1">
                        <Clock className="h-3 w-3" />
                        {test.duration} min
                      </Badge>
                      <Badge variant="outline" className="gap-1">
                        <Target className="h-3 w-3" />
                        {test.questions} questions
                      </Badge>
                    </div>
                    <div className="flex flex-wrap gap-4 text-sm text-gray-600 dark:text-gray-400">
                      <div>
                        Attempts: {test.attempts}/{test.maxAttempts}
                      </div>
                      {test.deadline && (
                        <div className="flex items-center gap-1">
                          <Calendar className="h-4 w-4" />
                          Due: {new Date(test.deadline).toLocaleDateString()}
                        </div>
                      )}
                      {test.status === 'completed' && test.score !== undefined && (
                        <div className="flex items-center gap-2">
                          <Progress value={test.score} className="w-24" />
                          <span className="font-semibold text-green-600 dark:text-green-400">
                            {test.score}/{test.maxScore}
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-2">
                  {test.status === 'not-started' && (
                    <Button 
                      className="bg-green-500 hover:bg-green-600 gap-2"
                      onClick={() => navigate(`/tests/${test.id}`)}
                    >
                      <Play className="h-4 w-4" />
                      Start Test
                    </Button>
                  )}
                  {test.status === 'in-progress' && (
                    <Button 
                      className="bg-yellow-500 hover:bg-yellow-600 gap-2"
                      onClick={() => navigate(`/tests/${test.id}`)}
                    >
                      <Play className="h-4 w-4" />
                      Continue
                    </Button>
                  )}
                  {test.status === 'completed' && (
                    <>
                      <Button 
                        variant="outline"
                        onClick={() => navigate(`/tests/${test.id}/results`)}
                      >
                        View Results
                      </Button>
                      {test.attempts < test.maxAttempts && (
                        <Button 
                          className="bg-green-500 hover:bg-green-600"
                          onClick={() => navigate(`/tests/${test.id}`)}
                        >
                          Retake
                        </Button>
                      )}
                    </>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>

        {(activeTab === 'available' ? availableTests : completedTests).length === 0 && (
          <Card className="p-12 text-center">
            <div className="flex flex-col items-center gap-4">
              <div className="bg-gray-100 dark:bg-gray-800 p-6 rounded-full">
                <FileText className="h-12 w-12 text-gray-400" />
              </div>
              <div>
                <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                  No tests {activeTab === 'available' ? 'available' : 'completed'} yet
                </h3>
                <p className="text-gray-600 dark:text-gray-400">
                  {activeTab === 'available'
                    ? 'Check back later for new assessments'
                    : 'Complete some tests to see your results here'}
                </p>
              </div>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
}
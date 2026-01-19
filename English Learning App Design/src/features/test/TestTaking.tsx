import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Progress } from '@/app/components/ui/progress';
import { Badge } from '@/app/components/ui/badge';
import { motion, AnimatePresence } from 'motion/react';
import {
  Clock,
  ChevronRight,
  ChevronLeft,
  Check,
  AlertCircle,
  Volume2,
  Flag,
} from 'lucide-react';
import { toast } from 'sonner';
import { RecordingButton } from '@/shared/ui/RecordingButton';
import { ConfettiCelebration } from '@/shared/ui/ConfettiCelebration';

interface TestQuestion {
  id: string;
  type: 'multiple-choice' | 'listening' | 'speaking' | 'fill-blank';
  question: string;
  audioUrl?: string;
  options?: string[];
  correctAnswer: string | number;
  points: number;
}

export function TestTaking() {
  const { testId } = useParams<{ testId: string }>();
  const navigate = useNavigate();
  
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<{ [key: string]: any }>({});
  const [timeRemaining, setTimeRemaining] = useState(1800); // 30 minutes in seconds
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [showExitConfirm, setShowExitConfirm] = useState(false);
  const [hasStarted, setHasStarted] = useState(false);

  // Mock test data based on testId
  const getTestData = (id: string | undefined) => {
    const testData: { [key: string]: { title: string; duration: number; questions: TestQuestion[] } } = {
      '1': {
        title: 'Mid-Term Listening Assessment',
        duration: 1800, // 30 minutes
        questions: [
          {
            id: 'q1',
            type: 'listening',
            question: 'Listen to the audio and choose the correct answer: What time does the meeting start?',
            audioUrl: 'https://example.com/audio1.mp3',
            options: ['9:00 AM', '10:00 AM', '11:00 AM', '2:00 PM'],
            correctAnswer: 1,
            points: 5,
          },
          {
            id: 'q2',
            type: 'listening',
            question: 'What is the main topic of the conversation?',
            audioUrl: 'https://example.com/audio2.mp3',
            options: ['Travel plans', 'Job interview', 'Restaurant booking', 'Birthday party'],
            correctAnswer: 2,
            points: 5,
          },
          {
            id: 'q3',
            type: 'listening',
            question: 'Where does the speaker suggest meeting?',
            audioUrl: 'https://example.com/audio3.mp3',
            options: ['At the coffee shop', 'At the office', 'At the park', 'At home'],
            correctAnswer: 0,
            points: 5,
          },
        ],
      },
      '2': {
        title: 'Speaking Fluency Test',
        duration: 900, // 15 minutes
        questions: [
          {
            id: 'q1',
            type: 'speaking',
            question: 'Introduce yourself in English (30 seconds)',
            correctAnswer: 'speaking-response',
            points: 20,
          },
          {
            id: 'q2',
            type: 'speaking',
            question: 'Describe your favorite hobby and why you enjoy it (45 seconds)',
            correctAnswer: 'speaking-response',
            points: 25,
          },
          {
            id: 'q3',
            type: 'speaking',
            question: 'Talk about your future career goals (60 seconds)',
            correctAnswer: 'speaking-response',
            points: 30,
          },
        ],
      },
      '3': {
        title: 'Vocabulary Quiz - Unit 3',
        duration: 1200, // 20 minutes
        questions: [
          {
            id: 'q1',
            type: 'multiple-choice',
            question: 'Choose the correct word: "I ____ to the store yesterday."',
            options: ['go', 'goes', 'went', 'going'],
            correctAnswer: 2,
            points: 3,
          },
          {
            id: 'q2',
            type: 'fill-blank',
            question: 'Fill in the blank: "She ____ a teacher for 10 years."',
            options: ['is', 'has been', 'was', 'will be'],
            correctAnswer: 1,
            points: 4,
          },
          {
            id: 'q3',
            type: 'multiple-choice',
            question: 'What is the meaning of "procrastinate"?',
            options: ['To delay doing something', 'To work quickly', 'To organize', 'To celebrate'],
            correctAnswer: 0,
            points: 3,
          },
        ],
      },
      '4': {
        title: 'Comprehensive Final Exam',
        duration: 3600, // 60 minutes
        questions: [
          {
            id: 'q1',
            type: 'listening',
            question: 'Listen to the audio and answer: What is the speaker\'s main concern?',
            audioUrl: 'https://example.com/audio-final1.mp3',
            options: ['Time management', 'Budget constraints', 'Team collaboration', 'Project deadlines'],
            correctAnswer: 3,
            points: 8,
          },
          {
            id: 'q2',
            type: 'multiple-choice',
            question: 'Choose the correct form: "If I ____ you, I would accept the offer."',
            options: ['am', 'was', 'were', 'be'],
            correctAnswer: 2,
            points: 6,
          },
          {
            id: 'q3',
            type: 'speaking',
            question: 'Present your opinion on remote work vs office work (90 seconds)',
            correctAnswer: 'speaking-response',
            points: 15,
          },
          {
            id: 'q4',
            type: 'fill-blank',
            question: 'Complete: "The project ____ completed by next month."',
            options: ['will', 'will be', 'would', 'would be'],
            correctAnswer: 1,
            points: 6,
          },
        ],
      },
    };

    return testData[id || '1'] || testData['1'];
  };

  const testData = getTestData(testId);
  const testQuestions = testData.questions;
  const currentQuestion = testQuestions[currentQuestionIndex];
  const totalQuestions = testQuestions.length;
  const progress = ((currentQuestionIndex + 1) / totalQuestions) * 100;

  // Update initial time based on test - only run once on mount
  useEffect(() => {
    setTimeRemaining(testData.duration);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Timer countdown - MOVED BEFORE ANY CONDITIONAL RETURNS
  useEffect(() => {
    if (!hasStarted) return;
    
    const timer = setInterval(() => {
      setTimeRemaining((prev) => {
        if (prev <= 0) {
          clearInterval(timer);
          toast.warning('Hết thời gian! Bài test sẽ được nộp tự động.');
          setTimeout(() => {
            setShowResults(true);
          }, 2000);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [hasStarted]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleAnswer = (answer: any) => {
    setAnswers((prev) => ({
      ...prev,
      [currentQuestion.id]: answer,
    }));
  };

  const handleNext = () => {
    if (currentQuestionIndex < totalQuestions - 1) {
      setCurrentQuestionIndex((prev) => prev + 1);
    }
  };

  const handlePrevious = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex((prev) => prev - 1);
    }
  };

  const handleSubmit = async () => {
    const unansweredCount = totalQuestions - Object.keys(answers).length;
    
    if (unansweredCount > 0 && !isSubmitting) {
      const confirm = window.confirm(
        `Bạn còn ${unansweredCount} câu chưa trả lời. Bạn có chắc muốn nộp bài?`
      );
      if (!confirm) return;
    }

    setIsSubmitting(true);

    // Simulate submission
    setTimeout(() => {
      setShowResults(true);
      setIsSubmitting(false);
    }, 2000);
  };

  const calculateScore = () => {
    let correctAnswers = 0;
    let totalPoints = 0;
    let earnedPoints = 0;

    testQuestions.forEach((q) => {
      totalPoints += q.points;
      if (answers[q.id] === q.correctAnswer) {
        correctAnswers++;
        earnedPoints += q.points;
      }
    });

    return {
      correctAnswers,
      totalQuestions,
      earnedPoints,
      totalPoints,
      percentage: Math.round((earnedPoints / totalPoints) * 100),
    };
  };

  const playAudio = () => {
    if (currentQuestion.audioUrl) {
      toast.info('Đang phát audio...');
      // Audio playback would be implemented here
    }
  };

  // Show start screen before test
  if (!hasStarted) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 dark:from-gray-900 dark:to-gray-800 p-4 md:p-8 flex items-center justify-center">
        <motion.div
          className="max-w-2xl w-full"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <Card className="p-8 md:p-12">
            <div className="text-center mb-8">
              <div className="inline-flex h-20 w-20 items-center justify-center rounded-full bg-green-100 dark:bg-green-900/30 mb-6">
                <Flag className="h-10 w-10 text-green-600 dark:text-green-400" />
              </div>
              <h1 className="text-3xl md:text-4xl font-bold mb-3">{testData.title}</h1>
              <p className="text-gray-600 dark:text-gray-400 text-lg">
                Chuẩn bị sẵn sàng cho bài kiểm tra của bạn
              </p>
            </div>

            <div className="space-y-4 mb-8">
              <Card className="p-4 bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800">
                <div className="flex items-center gap-3">
                  <Clock className="h-8 w-8 text-blue-600 dark:text-blue-400" />
                  <div>
                    <div className="font-semibold text-blue-900 dark:text-blue-100">
                      Thời gian: {Math.floor(testData.duration / 60)} phút
                    </div>
                    <div className="text-sm text-blue-700 dark:text-blue-300">
                      Bài test sẽ tự động nộp khi hết giờ
                    </div>
                  </div>
                </div>
              </Card>

              <Card className="p-4 bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800">
                <div className="flex items-center gap-3">
                  <AlertCircle className="h-8 w-8 text-green-600 dark:text-green-400" />
                  <div>
                    <div className="font-semibold text-green-900 dark:text-green-100">
                      Số câu hỏi: {testQuestions.length}
                    </div>
                    <div className="text-sm text-green-700 dark:text-green-300">
                      Bạn có thể quay lại câu trước đã làm
                    </div>
                  </div>
                </div>
              </Card>
            </div>

            <div className="bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded-lg p-4 mb-8">
              <h3 className="font-semibold text-yellow-900 dark:text-yellow-100 mb-2 flex items-center gap-2">
                <AlertCircle className="h-5 w-5" />
                Lưu ý quan trọng:
              </h3>
              <ul className="space-y-1 text-sm text-yellow-800 dark:text-yellow-200">
                <li>• Đảm bảo kết nối internet ổn định trong suốt bài test</li>
                <li>• Không thoát khỏi trang trong khi làm bài</li>
                <li>• Đối với câu hỏi Speaking, cần cho phép truy cập microphone</li>
                <li>• Điểm đạt yêu cầu: 70%</li>
              </ul>
            </div>

            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              <Button
                variant="outline"
                size="lg"
                onClick={() => navigate('/tests')}
              >
                Quay lại
              </Button>
              <Button
                size="lg"
                className="bg-green-500 hover:bg-green-600 text-white gap-2"
                onClick={() => setHasStarted(true)}
              >
                <Check className="h-5 w-5" />
                Bắt đầu làm bài
              </Button>
            </div>
          </Card>
        </motion.div>
      </div>
    );
  }

  if (showResults) {
    const score = calculateScore();
    const passed = score.percentage >= 70;

    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 to-green-100 dark:from-gray-900 dark:to-gray-800 p-4 md:p-8">
        {passed && <ConfettiCelebration />}
        
        <motion.div
          className="max-w-2xl mx-auto"
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
        >
          <Card className="p-8 text-center">
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ delay: 0.2, type: 'spring' }}
              className="mb-6"
            >
              <div className={`inline-flex h-24 w-24 items-center justify-center rounded-full ${
                passed ? 'bg-green-100 dark:bg-green-900/30' : 'bg-yellow-100 dark:bg-yellow-900/30'
              }`}>
                {passed ? (
                  <Check className="h-12 w-12 text-green-600 dark:text-green-400" />
                ) : (
                  <AlertCircle className="h-12 w-12 text-yellow-600 dark:text-yellow-400" />
                )}
              </div>
            </motion.div>

            <h1 className="text-3xl font-bold mb-2">
              {passed ? 'Chúc mừng! Bạn đã đạt!' : 'Cần cải thiện!'}
            </h1>
            <p className="text-gray-600 dark:text-gray-400 mb-8">
              {passed 
                ? 'Bạn đã hoàn thành xuất sắc bài kiểm tra này!' 
                : 'Đừng nản lòng, hãy ôn tập và thử lại nhé!'}
            </p>

            <div className="grid grid-cols-2 gap-4 mb-8">
              <Card className="p-4 bg-gradient-to-br from-blue-50 to-blue-100 dark:from-blue-900/20 dark:to-blue-800/20">
                <div className="text-3xl font-bold text-blue-600 dark:text-blue-400">
                  {score.percentage}%
                </div>
                <div className="text-sm text-gray-600 dark:text-gray-400">Điểm số</div>
              </Card>
              <Card className="p-4 bg-gradient-to-br from-green-50 to-green-100 dark:from-green-900/20 dark:to-green-800/20">
                <div className="text-3xl font-bold text-green-600 dark:text-green-400">
                  {score.correctAnswers}/{score.totalQuestions}
                </div>
                <div className="text-sm text-gray-600 dark:text-gray-400">Câu đúng</div>
              </Card>
            </div>

            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              <Button
                variant="outline"
                onClick={() => navigate('/tests')}
              >
                Về danh sách test
              </Button>
              {!passed && (
                <Button
                  className="bg-green-500 hover:bg-green-600"
                  onClick={() => window.location.reload()}
                >
                  Làm lại
                </Button>
              )}
              {passed && (
                <Button
                  className="bg-green-500 hover:bg-green-600"
                  onClick={() => navigate('/tests')}
                >
                  Xem kết quả chi tiết
                </Button>
              )}
            </div>
          </Card>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-white dark:bg-gray-900 border-b sticky top-0 z-10">
        <div className="max-w-4xl mx-auto p-4">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Badge variant="outline" className="gap-1">
                <Clock className="h-3 w-3" />
                {formatTime(timeRemaining)}
              </Badge>
              <Badge>
                Câu {currentQuestionIndex + 1}/{totalQuestions}
              </Badge>
            </div>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setShowExitConfirm(true)}
            >
              <Flag className="h-4 w-4 mr-2" />
              Nộp bài
            </Button>
          </div>
          <Progress value={progress} className="h-2" />
        </div>
      </div>

      {/* Question Content */}
      <div className="max-w-4xl mx-auto p-4 md:p-6">
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestionIndex}
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            transition={{ duration: 0.3 }}
          >
            <Card className="p-6 md:p-8 mb-6">
              {/* Question Type Badge */}
              <div className="mb-4">
                <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400">
                  {currentQuestion.type === 'multiple-choice' && 'Trắc nghiệm'}
                  {currentQuestion.type === 'listening' && 'Nghe hiểu'}
                  {currentQuestion.type === 'speaking' && 'Nói'}
                  {currentQuestion.type === 'fill-blank' && 'Điền từ'}
                </Badge>
              </div>

              {/* Question */}
              <h2 className="text-xl md:text-2xl font-bold mb-6">
                {currentQuestion.question}
              </h2>

              {/* Audio Player for Listening Questions */}
              {currentQuestion.type === 'listening' && currentQuestion.audioUrl && (
                <div className="mb-6">
                  <Button
                    onClick={playAudio}
                    className="bg-blue-500 hover:bg-blue-600 gap-2"
                  >
                    <Volume2 className="h-5 w-5" />
                    Phát audio
                  </Button>
                </div>
              )}

              {/* Answer Options */}
              {(currentQuestion.type === 'multiple-choice' || 
                currentQuestion.type === 'listening' || 
                currentQuestion.type === 'fill-blank') && (
                <div className="space-y-3">
                  {currentQuestion.options?.map((option, index) => (
                    <motion.div
                      key={index}
                      whileHover={{ scale: 1.02 }}
                      whileTap={{ scale: 0.98 }}
                    >
                      <Card
                        className={`p-4 cursor-pointer transition-all ${
                          answers[currentQuestion.id] === index
                            ? 'border-2 border-green-500 bg-green-50 dark:bg-green-900/20'
                            : 'hover:border-gray-300 dark:hover:border-gray-600'
                        }`}
                        onClick={() => handleAnswer(index)}
                      >
                        <div className="flex items-center gap-3">
                          <div className={`flex h-8 w-8 items-center justify-center rounded-full border-2 ${
                            answers[currentQuestion.id] === index
                              ? 'border-green-500 bg-green-500 text-white'
                              : 'border-gray-300 dark:border-gray-600'
                          }`}>
                            {answers[currentQuestion.id] === index && (
                              <Check className="h-5 w-5" />
                            )}
                          </div>
                          <span className="text-lg">{option}</span>
                        </div>
                      </Card>
                    </motion.div>
                  ))}
                </div>
              )}

              {/* Speaking Question */}
              {currentQuestion.type === 'speaking' && (
                <div className="flex flex-col items-center gap-4">
                  <div className="text-center mb-4">
                    <p className="text-gray-600 dark:text-gray-400">
                      Nhấn nút để bắt đầu ghi âm (tối đa 30 giây)
                    </p>
                  </div>
                  <RecordingButton
                    onRecordingComplete={(blob) => {
                      handleAnswer('recorded');
                      toast.success('Đã lưu bản ghi âm!');
                    }}
                    maxDuration={30}
                  />
                  {answers[currentQuestion.id] && (
                    <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400">
                      <Check className="h-4 w-4 mr-1" />
                      Đã ghi âm
                    </Badge>
                  )}
                </div>
              )}
            </Card>

            {/* Navigation */}
            <div className="flex items-center justify-between">
              <Button
                variant="outline"
                onClick={handlePrevious}
                disabled={currentQuestionIndex === 0}
              >
                <ChevronLeft className="h-4 w-4 mr-2" />
                Câu trước
              </Button>
              
              {currentQuestionIndex === totalQuestions - 1 ? (
                <Button
                  onClick={() => setShowExitConfirm(true)}
                  className="bg-green-500 hover:bg-green-600"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Đang nộp bài...' : 'Nộp bài'}
                  <Check className="h-4 w-4 ml-2" />
                </Button>
              ) : (
                <Button
                  onClick={handleNext}
                  className="bg-green-500 hover:bg-green-600"
                >
                  Câu sau
                  <ChevronRight className="h-4 w-4 ml-2" />
                </Button>
              )}
            </div>
          </motion.div>
        </AnimatePresence>

        {/* Answer Overview */}
        <Card className="mt-6 p-4">
          <h3 className="font-semibold mb-3">Tổng quan câu trả lời:</h3>
          <div className="flex flex-wrap gap-2">
            {testQuestions.map((q, index) => (
              <button
                key={q.id}
                onClick={() => setCurrentQuestionIndex(index)}
                className={`h-10 w-10 rounded-lg font-semibold transition-all ${
                  answers[q.id] !== undefined
                    ? 'bg-green-500 text-white'
                    : currentQuestionIndex === index
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300'
                }`}
              >
                {index + 1}
              </button>
            ))}
          </div>
        </Card>
      </div>

      {/* Exit Confirmation Dialog */}
      {showExitConfirm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
          >
            <Card className="p-6 max-w-md">
              <h3 className="text-xl font-bold mb-4">Xác nhận nộp bài</h3>
              <p className="text-gray-600 dark:text-gray-400 mb-6">
                Bạn đã trả lời {Object.keys(answers).length}/{totalQuestions} câu hỏi. 
                Bạn có chắc chắn muốn nộp bài không?
              </p>
              <div className="flex gap-3 justify-end">
                <Button
                  variant="outline"
                  onClick={() => setShowExitConfirm(false)}
                >
                  Tiếp tục làm bài
                </Button>
                <Button
                  className="bg-green-500 hover:bg-green-600"
                  onClick={handleSubmit}
                >
                  Nộp bài
                </Button>
              </div>
            </Card>
          </motion.div>
        </div>
      )}
    </div>
  );
}
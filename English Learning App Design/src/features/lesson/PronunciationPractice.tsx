import { useState, useRef, useEffect } from 'react';
import { useAppStore } from '@/store/appStore';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Badge } from '@/app/components/ui/badge';
import { Progress } from '@/app/components/ui/progress';
import { ConfettiCelebration } from '@/shared/ui/ConfettiCelebration';
import { RecordingButton } from '@/shared/ui/RecordingButton';
import { LoadingSpinner, SuccessCheckmark, ScaleButton } from '@/shared/ui/AnimationComponents';
import { 
  Mic, 
  MicOff, 
  Play,
  RotateCcw, 
  ChevronLeft,
  Volume2,
  Trophy,
  TrendingUp,
  CheckCircle,
  XCircle,
  AlertCircle
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { getLessonById } from '@/services/mockData';
import type { PronunciationResult, WordAnalysis, PronunciationScore } from '@/types';

// Mock pronunciation phrases
const mockPhrases = [
  {
    id: 'phrase-1',
    text: 'Hello! Nice to meet you.',
    pronunciation: '/həˈloʊ naɪs tuː miːt juː/',
  },
  {
    id: 'phrase-2',
    text: 'My name is Sarah.',
    pronunciation: '/maɪ neɪm ɪz ˈsærə/',
  },
  {
    id: 'phrase-3',
    text: "I'm from New York.",
    pronunciation: '/aɪm frɒm nuː jɔːrk/',
  },
  {
    id: 'phrase-4',
    text: 'What about you?',
    pronunciation: '/wɒt əˈbaʊt juː/',
  },
];

// Mock AI analysis function
const generateMockAnalysis = (targetText: string): PronunciationResult => {
  const words = targetText.split(' ');
  const wordAnalysis: WordAnalysis[] = words.map((word) => {
    const score = Math.floor(Math.random() * 30) + 70; // 70-100
    return {
      word: word.replace(/[,.!?]/g, ''),
      isCorrect: score >= 80,
      score,
      expectedPronunciation: '/example/',
      actualPronunciation: '/example/',
      feedback: score >= 90 ? 'Tuyệt vời!' : score >= 80 ? 'Tốt' : 'Cần cải thiện',
    };
  });

  const overall = Math.floor(wordAnalysis.reduce((sum, w) => sum + w.score, 0) / words.length);
  
  return {
    score: {
      overall,
      pronunciation: overall + Math.floor(Math.random() * 5),
      fluency: overall - Math.floor(Math.random() * 5),
      intonation: overall + Math.floor(Math.random() * 3),
      accuracy: overall - Math.floor(Math.random() * 3),
    },
    transcript: targetText,
    targetText,
    wordAnalysis,
    suggestions: overall < 85 
      ? ['Nói chậm hơn một chút', 'Chú ý phát âm chính xác từng từ', 'Luyện tập thêm âm /θ/ và /ð/']
      : ['Tiếp tục duy trì', 'Thử tăng tốc độ nói'],
    strengths: overall >= 85
      ? ['Phát âm rõ ràng', 'Ngữ điệu tự nhiên', 'Độ trôi chảy tốt']
      : ['Cố gắng tốt', 'Âm thanh rõ ràng'],
    weaknesses: overall < 85
      ? ['Cần cải thiện độ chính xác', 'Ngữ điệu cần tự nhiên hơn']
      : [],
  };
};

export function PronunciationPractice() {
  const { selectedLessonId, setCurrentView, completeLesson, addXP } = useAppStore();
  const lesson = getLessonById(selectedLessonId || '');
  
  const [currentPhraseIndex, setCurrentPhraseIndex] = useState(0);
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [hasRecorded, setHasRecorded] = useState(false);
  const [analysisResult, setAnalysisResult] = useState<PronunciationResult | null>(null);
  const [showResult, setShowResult] = useState(false);
  const recordingTimerRef = useRef<NodeJS.Timeout | null>(null);

  const currentPhrase = mockPhrases[currentPhraseIndex];
  const totalPhrases = mockPhrases.length;
  const progress = ((currentPhraseIndex + 1) / totalPhrases) * 100;

  // Recording timer
  useEffect(() => {
    if (isRecording) {
      recordingTimerRef.current = setInterval(() => {
        setRecordingTime((prev) => {
          if (prev >= 10) {
            handleStopRecording();
            return 10;
          }
          return prev + 0.1;
        });
      }, 100);
    } else {
      if (recordingTimerRef.current) {
        clearInterval(recordingTimerRef.current);
      }
    }
    return () => {
      if (recordingTimerRef.current) {
        clearInterval(recordingTimerRef.current);
      }
    };
  }, [isRecording]);

  const handleStartRecording = async () => {
    // In a real app, request microphone permission here
    setIsRecording(true);
    setRecordingTime(0);
    setHasRecorded(false);
    setShowResult(false);
  };

  const handleStopRecording = () => {
    setIsRecording(false);
    setHasRecorded(true);
    
    // Simulate AI analysis after a delay
    setTimeout(() => {
      const result = generateMockAnalysis(currentPhrase.text);
      setAnalysisResult(result);
      setShowResult(true);
    }, 1500);
  };

  const handlePlayExample = () => {
    // In a real app, play audio here
    console.log('Playing example audio');
  };

  const handleNextPhrase = () => {
    if (currentPhraseIndex < totalPhrases - 1) {
      setCurrentPhraseIndex(currentPhraseIndex + 1);
      setRecordingTime(0);
      setHasRecorded(false);
      setShowResult(false);
      setAnalysisResult(null);
    } else {
      // Complete lesson
      const avgScore = analysisResult?.score.overall || 85;
      completeLesson(selectedLessonId || '', avgScore);
      addXP(75);
      setCurrentView('session-detail');
    }
  };

  const handleRetry = () => {
    setRecordingTime(0);
    setHasRecorded(false);
    setShowResult(false);
    setAnalysisResult(null);
  };

  const getScoreColor = (score: number) => {
    if (score >= 90) return 'text-green-600 dark:text-green-400';
    if (score >= 75) return 'text-yellow-600 dark:text-yellow-400';
    return 'text-red-600 dark:text-red-400';
  };

  const getScoreBg = (score: number) => {
    if (score >= 90) return 'bg-green-100 dark:bg-green-900/20';
    if (score >= 75) return 'bg-yellow-100 dark:bg-yellow-900/20';
    return 'bg-red-100 dark:bg-red-900/20';
  };

  if (!lesson) {
    return (
      <div className="container mx-auto p-4">
        <p>Không tìm thấy bài học</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-emerald-50 to-teal-50 dark:from-gray-900 dark:via-gray-900 dark:to-gray-900">
      <div className="container mx-auto px-4 py-6 max-w-4xl">
        {/* Header */}
        <div className="mb-6 flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={() => setCurrentView('session-detail')}
            className="gap-2"
          >
            <ChevronLeft className="h-4 w-4" />
            Quay lại
          </Button>
          <Badge variant="outline" className="gap-1">
            <Mic className="h-3 w-3" />
            Pronunciation Practice
          </Badge>
        </div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          {/* Title */}
          <div className="mb-6">
            <h1 className="mb-2 text-3xl font-bold">{lesson.title}</h1>
            <p className="text-gray-600 dark:text-gray-400 mb-4">{lesson.description}</p>
            
            {/* Progress */}
            <div className="space-y-2">
              <div className="flex items-center justify-between text-sm">
                <span className="font-medium">
                  Câu {currentPhraseIndex + 1} / {totalPhrases}
                </span>
                <span className="text-gray-600 dark:text-gray-400">
                  {Math.round(progress)}%
                </span>
              </div>
              <Progress value={progress} className="h-2" />
            </div>
          </div>

          {/* Main Practice Card */}
          <Card className="mb-6 overflow-hidden">
            <div className="p-6 md:p-8">
              {/* Target Phrase */}
              <div className="mb-6 text-center">
                <div className="mb-4 flex items-center justify-center">
                  <Button
                    size="lg"
                    variant="outline"
                    onClick={handlePlayExample}
                    className="gap-2"
                  >
                    <Volume2 className="h-5 w-5" />
                    Nghe mẫu
                  </Button>
                </div>
                
                <motion.div
                  key={currentPhrase.id}
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="mb-3"
                >
                  <p className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-white mb-2">
                    {currentPhrase.text}
                  </p>
                  <p className="text-lg text-gray-600 dark:text-gray-400">
                    {currentPhrase.pronunciation}
                  </p>
                </motion.div>
              </div>

              {/* Recording Controls */}
              <div className="flex flex-col items-center gap-4">
                <AnimatePresence mode="wait">
                  {!hasRecorded ? (
                    <motion.div
                      key="record-button"
                      initial={{ scale: 0 }}
                      animate={{ scale: 1 }}
                      exit={{ scale: 0 }}
                      className="w-full flex justify-center"
                    >
                      <RecordingButton
                        isRecording={isRecording}
                        recordingTime={recordingTime}
                        maxDuration={10}
                        onStartRecording={handleStartRecording}
                        onStopRecording={handleStopRecording}
                        size="lg"
                        showWaveform={true}
                      />
                    </motion.div>
                  ) : (
                    <motion.div
                      key="analyzing"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      className="flex flex-col items-center gap-4 py-8"
                    >
                      {!showResult ? (
                        <>
                          <LoadingSpinner size="lg" className="mb-4" />
                          <p className="text-lg font-medium">Đang phân tích phát âm của bạn...</p>
                          <p className="text-sm text-gray-500 dark:text-gray-400">AI đang xử lý...</p>
                        </>
                      ) : null}
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </div>
          </Card>

          {/* Results */}
          <AnimatePresence>
            {showResult && analysisResult && (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                className="space-y-4"
              >
                {/* Overall Score */}
                <Card className={`p-6 ${getScoreBg(analysisResult.score.overall)}`}>
                  <div className="flex items-center justify-between">
                    <div>
                      <h2 className="text-lg font-bold mb-1">Điểm tổng quan</h2>
                      <p className="text-sm text-gray-600 dark:text-gray-400">
                        {analysisResult.score.overall >= 90 ? 'Xuất sắc! 🎉' : 
                         analysisResult.score.overall >= 75 ? 'Tốt! 👍' : 
                         'Cần cải thiện 💪'}
                      </p>
                    </div>
                    <div className="text-center">
                      <motion.div
                        initial={{ scale: 0 }}
                        animate={{ scale: 1 }}
                        transition={{ type: 'spring', stiffness: 200 }}
                        className={`text-5xl font-bold ${getScoreColor(analysisResult.score.overall)}`}
                      >
                        {analysisResult.score.overall}
                      </motion.div>
                      <Trophy className={`h-8 w-8 mx-auto mt-2 ${getScoreColor(analysisResult.score.overall)}`} />
                    </div>
                  </div>
                </Card>

                {/* Detailed Scores */}
                <Card className="p-6">
                  <h3 className="mb-4 font-bold text-lg">Chi tiết đánh giá</h3>
                  <div className="grid gap-4 md:grid-cols-2">
                    {[
                      { label: 'Phát âm', score: analysisResult.score.pronunciation, icon: Mic },
                      { label: 'Độ trôi chảy', score: analysisResult.score.fluency, icon: TrendingUp },
                      { label: 'Ngữ điệu', score: analysisResult.score.intonation, icon: Volume2 },
                      { label: 'Độ chính xác', score: analysisResult.score.accuracy, icon: CheckCircle },
                    ].map((item) => (
                      <div key={item.label} className="space-y-2">
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            <item.icon className="h-4 w-4 text-gray-600 dark:text-gray-400" />
                            <span className="text-sm font-medium">{item.label}</span>
                          </div>
                          <span className={`font-bold ${getScoreColor(item.score)}`}>
                            {item.score}
                          </span>
                        </div>
                        <Progress value={item.score} className="h-2" />
                      </div>
                    ))}
                  </div>
                </Card>

                {/* Word Analysis */}
                <Card className="p-6">
                  <h3 className="mb-4 font-bold text-lg">Phân tích từng từ</h3>
                  <div className="flex flex-wrap gap-2">
                    {analysisResult.wordAnalysis.map((word, index) => (
                      <motion.div
                        key={index}
                        initial={{ opacity: 0, scale: 0.8 }}
                        animate={{ opacity: 1, scale: 1 }}
                        transition={{ delay: index * 0.1 }}
                        className={`flex items-center gap-2 rounded-lg px-3 py-2 ${
                          word.isCorrect
                            ? 'bg-green-100 dark:bg-green-900/20 text-green-700 dark:text-green-400'
                            : 'bg-yellow-100 dark:bg-yellow-900/20 text-yellow-700 dark:text-yellow-400'
                        }`}
                      >
                        {word.isCorrect ? (
                          <CheckCircle className="h-4 w-4" />
                        ) : (
                          <AlertCircle className="h-4 w-4" />
                        )}
                        <span className="font-medium">{word.word}</span>
                        <Badge variant="outline" className="text-xs">
                          {word.score}
                        </Badge>
                      </motion.div>
                    ))}
                  </div>
                </Card>

                {/* Feedback */}
                <div className="grid gap-4 md:grid-cols-2">
                  {/* Strengths */}
                  {analysisResult.strengths.length > 0 && (
                    <Card className="p-4 bg-green-50 dark:bg-green-900/10 border-green-200 dark:border-green-800">
                      <h4 className="mb-2 font-bold flex items-center gap-2 text-green-700 dark:text-green-400">
                        <CheckCircle className="h-5 w-5" />
                        Điểm mạnh
                      </h4>
                      <ul className="space-y-1 text-sm">
                        {analysisResult.strengths.map((strength, index) => (
                          <li key={index} className="text-green-700 dark:text-green-400">
                            • {strength}
                          </li>
                        ))}
                      </ul>
                    </Card>
                  )}

                  {/* Suggestions */}
                  {analysisResult.suggestions.length > 0 && (
                    <Card className="p-4 bg-blue-50 dark:bg-blue-900/10 border-blue-200 dark:border-blue-800">
                      <h4 className="mb-2 font-bold flex items-center gap-2 text-blue-700 dark:text-blue-400">
                        <TrendingUp className="h-5 w-5" />
                        Gợi ý cải thiện
                      </h4>
                      <ul className="space-y-1 text-sm">
                        {analysisResult.suggestions.map((suggestion, index) => (
                          <li key={index} className="text-blue-700 dark:text-blue-400">
                            • {suggestion}
                          </li>
                        ))}
                      </ul>
                    </Card>
                  )}
                </div>

                {/* Action Buttons */}
                <div className="flex gap-3">
                  <Button
                    variant="outline"
                    onClick={handleRetry}
                    className="flex-1 gap-2"
                  >
                    <RotateCcw className="h-4 w-4" />
                    Thử lại
                  </Button>
                  <Button
                    onClick={handleNextPhrase}
                    className="flex-1 gap-2 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600"
                  >
                    {currentPhraseIndex < totalPhrases - 1 ? (
                      <>
                        Câu tiếp theo
                        <ChevronLeft className="h-4 w-4 rotate-180" />
                      </>
                    ) : (
                      <>
                        <CheckCircle className="h-4 w-4" />
                        Hoàn thành (+75 XP)
                      </>
                    )}
                  </Button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </motion.div>
      </div>
    </div>
  );
}
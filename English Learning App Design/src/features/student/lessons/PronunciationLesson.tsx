import { useState, useRef, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Progress } from '@/app/components/ui/progress';
import { Badge } from '@/app/components/ui/badge';
import { Mic, Volume2, RotateCcw, CheckCircle2, XCircle, ArrowLeft, ArrowRight, Trophy } from 'lucide-react';
import { toast } from 'sonner';

interface Word {
  id: number;
  word: string;
  phonetic: string;
  audioUrl: string;
  difficulty: 'easy' | 'medium' | 'hard';
}

export function PronunciationLesson() {
  const navigate = useNavigate();
  const { lessonId } = useParams();
  
  const [currentWordIndex, setCurrentWordIndex] = useState(0);
  const [isRecording, setIsRecording] = useState(false);
  const [scores, setScores] = useState<number[]>([]);
  const [attempts, setAttempts] = useState(0);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  const words: Word[] = [
    {
      id: 1,
      word: 'Restaurant',
      phonetic: '/ˈrestərɑːnt/',
      audioUrl: '/audio/restaurant.mp3',
      difficulty: 'medium',
    },
    {
      id: 2,
      word: 'Pronunciation',
      phonetic: '/prəˌnʌnsiˈeɪʃn/',
      audioUrl: '/audio/pronunciation.mp3',
      difficulty: 'hard',
    },
    {
      id: 3,
      word: 'Beautiful',
      phonetic: '/ˈbjuːtɪfl/',
      audioUrl: '/audio/beautiful.mp3',
      difficulty: 'easy',
    },
  ];

  const currentWord = words[currentWordIndex];
  const progress = ((currentWordIndex + 1) / words.length) * 100;

  // Cleanup audio on unmount or when changing words
  useEffect(() => {
    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.src = '';
        audioRef.current = null;
      }
    };
  }, [currentWordIndex]);

  const playAudio = () => {
    // Clean up previous audio instance if exists
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.src = '';
      audioRef.current = null;
    }

    const audio = new Audio(currentWord.audioUrl);
    audioRef.current = audio;
    
    const playPromise = audio.play();
    
    if (playPromise !== undefined) {
      playPromise
        .then(() => {
          // Audio played successfully
        })
        .catch((error) => {
          console.log('Audio playback failed, using speech synthesis fallback:', error);
          // Fallback: use speech synthesis
          const utterance = new SpeechSynthesisUtterance(currentWord.word);
          utterance.lang = 'en-US';
          utterance.rate = 0.8;
          window.speechSynthesis.speak(utterance);
        });
    }
  };

  const startRecording = async () => {
    setIsRecording(true);
    setAttempts(attempts + 1);

    // Simulate recording for 2 seconds
    setTimeout(() => {
      setIsRecording(false);
      
      // Mock AI scoring (70-100)
      const score = Math.floor(Math.random() * 30) + 70;
      const newScores = [...scores];
      newScores[currentWordIndex] = Math.max(score, newScores[currentWordIndex] || 0);
      setScores(newScores);

      if (score >= 85) {
        toast.success(`Excellent! Score: ${score}/100 🎉`);
      } else if (score >= 70) {
        toast.success(`Good! Score: ${score}/100. Try again for better score!`);
      } else {
        toast.error(`Score: ${score}/100. Listen and try again!`);
      }
    }, 2000);
  };

  const handleNext = () => {
    if (currentWordIndex < words.length - 1) {
      setCurrentWordIndex(currentWordIndex + 1);
      setAttempts(0);
    }
  };

  const handlePrevious = () => {
    if (currentWordIndex > 0) {
      setCurrentWordIndex(currentWordIndex - 1);
      setAttempts(0);
    }
  };

  const handleComplete = () => {
    const avgScore = scores.reduce((a, b) => a + b, 0) / scores.length;
    const xp = Math.floor(avgScore / 2);
    toast.success(`🎉 Lesson Completed! +${xp} XP`);
    navigate('/student/learning');
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'easy': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300';
      case 'medium': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300';
      case 'hard': return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300';
      default: return '';
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 85) return 'text-green-600 dark:text-green-400';
    if (score >= 70) return 'text-yellow-600 dark:text-yellow-400';
    return 'text-red-600 dark:text-red-400';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 to-pink-50 dark:from-gray-900 dark:to-gray-800 p-6">
      <div className="max-w-3xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={() => navigate('/student/learning')}
            className="gap-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Back
          </Button>
          <div className="flex items-center gap-2">
            <Trophy className="w-5 h-5 text-yellow-600" />
            <span className="font-semibold">Attempts: {attempts}</span>
          </div>
        </div>

        {/* Progress */}
        <div className="space-y-2">
          <div className="flex justify-between text-sm text-gray-600 dark:text-gray-400">
            <span>Progress</span>
            <span>{currentWordIndex + 1} / {words.length}</span>
          </div>
          <Progress value={progress} className="h-3" />
        </div>

        {/* Main Card */}
        <Card className="p-8">
          <div className="text-center space-y-6">
            {/* Difficulty Badge */}
            <Badge className={getDifficultyColor(currentWord.difficulty)}>
              {currentWord.difficulty.toUpperCase()}
            </Badge>

            {/* Word Display */}
            <div className="space-y-4">
              <h1 className="text-6xl font-bold text-gray-900 dark:text-white">
                {currentWord.word}
              </h1>
              <p className="text-2xl text-gray-600 dark:text-gray-400">
                {currentWord.phonetic}
              </p>
            </div>

            {/* Listen Button */}
            <Button
              onClick={playAudio}
              variant="outline"
              size="lg"
              className="gap-2"
            >
              <Volume2 className="w-5 h-5" />
              Listen
            </Button>

            {/* Recording Section */}
            <div className="py-8">
              {isRecording ? (
                <div className="flex flex-col items-center gap-4">
                  <div className="w-32 h-32 rounded-full bg-red-500 animate-pulse flex items-center justify-center">
                    <Mic className="w-16 h-16 text-white" />
                  </div>
                  <p className="text-lg font-semibold text-red-600">Recording...</p>
                </div>
              ) : (
                <Button
                  onClick={startRecording}
                  size="lg"
                  className="w-32 h-32 rounded-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700"
                >
                  <Mic className="w-12 h-12" />
                </Button>
              )}
              <p className="text-sm text-gray-600 dark:text-gray-400 mt-4">
                Click the microphone to practice
              </p>
            </div>

            {/* Score Display */}
            {scores[currentWordIndex] !== undefined && (
              <div className="bg-gray-50 dark:bg-gray-800 rounded-lg p-6">
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
                  Your Best Score
                </p>
                <div className="flex items-center justify-center gap-3">
                  {scores[currentWordIndex] >= 85 ? (
                    <CheckCircle2 className="w-8 h-8 text-green-600" />
                  ) : (
                    <RotateCcw className="w-8 h-8 text-yellow-600" />
                  )}
                  <p className={`text-5xl font-bold ${getScoreColor(scores[currentWordIndex])}`}>
                    {scores[currentWordIndex]}
                    <span className="text-2xl">/100</span>
                  </p>
                </div>
                {scores[currentWordIndex] < 85 && (
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-2">
                    Try again to improve your score!
                  </p>
                )}
              </div>
            )}
          </div>
        </Card>

        {/* Navigation */}
        <div className="flex justify-between items-center">
          <Button
            variant="outline"
            onClick={handlePrevious}
            disabled={currentWordIndex === 0}
            className="gap-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Previous
          </Button>

          {currentWordIndex === words.length - 1 ? (
            <Button
              onClick={handleComplete}
              className="bg-green-600 hover:bg-green-700 text-white gap-2"
              disabled={scores.filter(s => s >= 70).length < words.length}
            >
              <CheckCircle2 className="w-5 h-5" />
              Complete Lesson
            </Button>
          ) : (
            <Button
              onClick={handleNext}
              disabled={!scores[currentWordIndex] || scores[currentWordIndex] < 70}
              className="gap-2"
            >
              Next
              <ArrowRight className="w-4 h-4" />
            </Button>
          )}
        </div>

        {/* Score Overview */}
        <Card className="p-6">
          <h3 className="font-semibold mb-4">Your Progress</h3>
          <div className="grid grid-cols-3 gap-4">
            {words.map((word, idx) => (
              <div
                key={word.id}
                className={`p-4 rounded-lg border-2 ${
                  idx === currentWordIndex
                    ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20'
                    : scores[idx]
                    ? 'border-green-500 bg-green-50 dark:bg-green-900/20'
                    : 'border-gray-200 dark:border-gray-700'
                }`}
              >
                <p className="text-sm font-semibold mb-1">{word.word}</p>
                {scores[idx] ? (
                  <p className={`text-2xl font-bold ${getScoreColor(scores[idx])}`}>
                    {scores[idx]}
                  </p>
                ) : (
                  <p className="text-sm text-gray-400">Not attempted</p>
                )}
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}
import { useState, useRef } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Progress } from '@/app/components/ui/progress';
import { Badge } from '@/app/components/ui/badge';
import { Play, Pause, RotateCcw, Volume2, VolumeX, CheckCircle2, ArrowLeft, ArrowRight } from 'lucide-react';
import { toast } from 'sonner';
import { useCompleteLesson } from '@/hooks/useCompleteLesson';

interface Subtitle {
  time: number;
  speaker: string;
  text: string;
  translation: string;
}

export function VideoConversationLesson() {
  const navigate = useNavigate();
  const { lessonId } = useParams();
  const { completeAndSave } = useCompleteLesson();
  const videoRef = useRef<HTMLVideoElement>(null);
  
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMuted, setIsMuted] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [showTranslation, setShowTranslation] = useState(false);
  const [currentSubtitle, setCurrentSubtitle] = useState<Subtitle | null>(null);
  const [completedSegments, setCompletedSegments] = useState<number[]>([]);

  // Mock data
  const lesson = {
    id: lessonId,
    title: 'Ordering Food at a Restaurant',
    level: 'Intermediate',
    duration: '5:30',
    xp: 50,
  };

  const subtitles: Subtitle[] = [
    {
      time: 0,
      speaker: 'Waiter',
      text: 'Good evening! Welcome to our restaurant.',
      translation: 'Chào buổi tối! Chào mừng đến nhà hàng của chúng tôi.',
    },
    {
      time: 3,
      speaker: 'Customer',
      text: 'Good evening. Table for two, please.',
      translation: 'Chào buổi tối. Cho tôi bàn cho hai người.',
    },
    {
      time: 6,
      speaker: 'Waiter',
      text: 'Of course. Right this way, please.',
      translation: 'Tất nhiên. Mời đi theo tôi.',
    },
  ];

  const questions = [
    {
      id: 1,
      question: 'How many people are dining?',
      options: ['One', 'Two', 'Three', 'Four'],
      correct: 1,
    },
    {
      id: 2,
      question: 'What time of day is it?',
      options: ['Morning', 'Afternoon', 'Evening', 'Night'],
      correct: 2,
    },
  ];

  const togglePlay = () => {
    if (videoRef.current) {
      if (isPlaying) {
        videoRef.current.pause();
      } else {
        videoRef.current.play();
      }
      setIsPlaying(!isPlaying);
    }
  };

  const toggleMute = () => {
    if (videoRef.current) {
      videoRef.current.muted = !isMuted;
      setIsMuted(!isMuted);
    }
  };

  const handleReplay = () => {
    if (videoRef.current) {
      videoRef.current.currentTime = 0;
      videoRef.current.play();
      setIsPlaying(true);
    }
  };

  const handleTimeUpdate = () => {
    if (videoRef.current) {
      const current = videoRef.current.currentTime;
      setCurrentTime(current);
      
      // Find current subtitle
      const subtitle = subtitles.find(
        (s, i) => current >= s.time && (i === subtitles.length - 1 || current < subtitles[i + 1].time)
      );
      setCurrentSubtitle(subtitle || null);
    }
  };

  const handleComplete = async () => {
    if (!lessonId) return;
    const ok = await completeAndSave(lessonId, 85);
    if (ok) {
      toast.success(`🎉 Completed! +${lesson.xp} XP`);
      navigate('/student/learning');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 dark:from-gray-900 dark:to-gray-800 p-6">
      <div className="max-w-5xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={() => navigate('/student/learning')}
            className="gap-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Lessons
          </Button>
          <Badge variant="secondary" className="text-lg px-4 py-2">
            {lesson.level}
          </Badge>
        </div>

        {/* Video Section */}
        <Card className="overflow-hidden">
          <div className="bg-black aspect-video relative">
            <video
              ref={videoRef}
              className="w-full h-full"
              onTimeUpdate={handleTimeUpdate}
              onLoadedMetadata={(e) => setDuration(e.currentTarget.duration)}
              poster="https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800"
            >
              <source src="/videos/restaurant-conversation.mp4" type="video/mp4" />
            </video>

            {/* Subtitles Overlay */}
            {currentSubtitle && (
              <div className="absolute bottom-20 left-0 right-0 px-8">
                <div className="bg-black/80 backdrop-blur-sm rounded-lg p-4 text-center">
                  <p className="text-white text-lg font-semibold mb-1">
                    {currentSubtitle.speaker}
                  </p>
                  <p className="text-white text-xl">
                    {currentSubtitle.text}
                  </p>
                  {showTranslation && (
                    <p className="text-green-400 text-sm mt-2">
                      {currentSubtitle.translation}
                    </p>
                  )}
                </div>
              </div>
            )}

            {/* Video Controls */}
            <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/80 to-transparent p-6">
              <Progress value={(currentTime / duration) * 100} className="mb-4" />
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={togglePlay}
                    className="text-white hover:bg-white/20"
                  >
                    {isPlaying ? <Pause className="w-6 h-6" /> : <Play className="w-6 h-6" />}
                  </Button>
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={handleReplay}
                    className="text-white hover:bg-white/20"
                  >
                    <RotateCcw className="w-5 h-5" />
                  </Button>
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={toggleMute}
                    className="text-white hover:bg-white/20"
                  >
                    {isMuted ? <VolumeX className="w-5 h-5" /> : <Volume2 className="w-5 h-5" />}
                  </Button>
                  <span className="text-white text-sm">
                    {Math.floor(currentTime / 60)}:{String(Math.floor(currentTime % 60)).padStart(2, '0')} / {lesson.duration}
                  </span>
                </div>
                <Button
                  variant="secondary"
                  onClick={() => setShowTranslation(!showTranslation)}
                  className="text-sm"
                >
                  {showTranslation ? 'Hide' : 'Show'} Translation
                </Button>
              </div>
            </div>
          </div>
        </Card>

        {/* Comprehension Questions */}
        <Card className="p-6">
          <h2 className="text-2xl font-bold mb-6">Comprehension Check</h2>
          <div className="space-y-6">
            {questions.map((q, idx) => (
              <div key={q.id} className="space-y-3">
                <p className="font-semibold text-lg">
                  {idx + 1}. {q.question}
                </p>
                <div className="grid grid-cols-2 gap-3">
                  {q.options.map((option, optIdx) => (
                    <Button
                      key={optIdx}
                      variant="outline"
                      className="justify-start h-auto py-3 px-4"
                      onClick={() => {
                        if (optIdx === q.correct) {
                          toast.success('Correct! 🎉');
                          setCompletedSegments([...completedSegments, q.id]);
                        } else {
                          toast.error('Try again!');
                        }
                      }}
                    >
                      {option}
                    </Button>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Action Buttons */}
        <div className="flex justify-between items-center">
          <Button variant="outline" onClick={() => navigate('/student/learning')}>
            Save Progress
          </Button>
          <Button
            onClick={handleComplete}
            className="bg-green-600 hover:bg-green-700 text-white gap-2"
            disabled={completedSegments.length < questions.length}
          >
            <CheckCircle2 className="w-5 h-5" />
            Complete Lesson (+{lesson.xp} XP)
            <ArrowRight className="w-4 h-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}

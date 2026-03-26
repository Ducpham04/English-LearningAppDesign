import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Progress } from '@/app/components/ui/progress';
import { Badge } from '@/app/components/ui/badge';
import { RotateCcw, CheckCircle2, ArrowLeft, ArrowRight, Volume2, Star, Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { motion, AnimatePresence } from 'motion/react';
import { useCompleteLesson } from '@/hooks/useCompleteLesson';
import apiService from '@/services/apiService';

interface Flashcard {
  id: number;
  word: string;
  translation: string;
  phonetic: string;
  example: string;
  exampleTranslation: string;
  image?: string;
}

function mapVocabRowsToFlashcards(rows: unknown[]): Flashcard[] {
  if (!Array.isArray(rows)) return [];
  return rows.map((row: any, idx: number) => {
    const id = row.vocabularyId != null ? Number(row.vocabularyId) : idx + 1;
    const word = String(row.word ?? '').trim();
    let translation = '';
    let exampleEn = '';
    let exampleVi = '';
    const meanings = row.meanings;
    if (Array.isArray(meanings) && meanings.length > 0) {
      const m = meanings[0] as Record<string, string>;
      translation = String(m?.def ?? m?.definition ?? '').trim();
      const exRaw = String(m?.ex ?? m?.example ?? '').trim();
      if (exRaw) {
        const seps = [' — ', ' – ', ' - '];
        let parts: string[] | null = null;
        for (const s of seps) {
          if (exRaw.includes(s)) {
            parts = exRaw.split(s).map((x: string) => x.trim()).filter(Boolean);
            break;
          }
        }
        if (parts && parts.length >= 2) {
          exampleEn = parts[0];
          exampleVi = parts.slice(1).join(' — ');
        } else {
          exampleEn = exRaw;
        }
      }
    }
    const phoneticRaw = row.phonetic != null ? String(row.phonetic).trim() : '';
    const phoneticDisplay =
      phoneticRaw === ''
        ? ''
        : phoneticRaw.startsWith('/')
          ? phoneticRaw
          : `/${phoneticRaw.replace(/^\//, '').replace(/\/$/, '')}/`;

    return {
      id,
      word: word || `Card ${idx + 1}`,
      translation: translation || '—',
      phonetic: phoneticDisplay,
      example: exampleEn || '—',
      exampleTranslation: exampleVi,
    };
  });
}

export function FlashcardLesson() {
  const navigate = useNavigate();
  const { lessonId } = useParams();
  const { completeAndSave } = useCompleteLesson();

  const [cards, setCards] = useState<Flashcard[]>([]);
  const [loading, setLoading] = useState(true);
  const [lessonTitle, setLessonTitle] = useState<string>('');

  const [currentIndex, setCurrentIndex] = useState(0);
  const [isFlipped, setIsFlipped] = useState(false);
  const [known, setKnown] = useState<number[]>([]);
  const [learning, setLearning] = useState<number[]>([]);

  const loadVocab = useCallback(async () => {
    if (!lessonId) {
      setLoading(false);
      return;
    }
    setLoading(true);
    try {
      const [lessonRes, vocabRes] = await Promise.all([
        apiService.lesson.getById(Number(lessonId)),
        apiService.lesson.getVocabularies(Number(lessonId)),
      ]);
      const lesson = (lessonRes.data as any)?.data ?? lessonRes.data;
      const rawList = (vocabRes.data as any)?.data ?? (vocabRes.data as any);
      const list = Array.isArray(rawList) ? rawList : [];
      setLessonTitle(String(lesson?.title ?? 'Flashcard'));
      const mapped = mapVocabRowsToFlashcards(list);
      setCards(mapped);
      setCurrentIndex(0);
      setIsFlipped(false);
      setKnown([]);
      setLearning([]);
      if (mapped.length === 0) {
        toast.info('Bài học chưa có từ vựng', {
          description: 'Giáo viên hoặc admin cần thêm từ trong bài VOCAB.',
        });
      }
    } catch (e: any) {
      toast.error('Không tải được từ vựng', {
        description: e?.response?.data?.message ?? e?.message ?? 'Thử lại sau.',
      });
      setCards([]);
    } finally {
      setLoading(false);
    }
  }, [lessonId]);

  useEffect(() => {
    void loadVocab();
  }, [loadVocab]);

  const currentCard = cards[currentIndex];
  const n = Math.max(cards.length, 1);
  const progress = ((currentIndex + 1) / n) * 100;

  const handleFlip = () => {
    setIsFlipped(!isFlipped);
  };

  const handleKnown = () => {
    if (!currentCard) return;
    if (!known.includes(currentCard.id)) {
      setKnown([...known, currentCard.id]);
      toast.success('Great! Card marked as known 🎉');
    }
    handleNext();
  };

  const handleLearning = () => {
    if (!currentCard) return;
    if (!learning.includes(currentCard.id)) {
      setLearning([...learning, currentCard.id]);
      toast.info('Card added to learning pile 📚');
    }
    handleNext();
  };

  const handleNext = () => {
    if (currentIndex < cards.length - 1) {
      setCurrentIndex(currentIndex + 1);
      setIsFlipped(false);
    }
  };

  const handlePrevious = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
      setIsFlipped(false);
    }
  };

  const playAudio = () => {
    if (!currentCard?.word) return;
    const utterance = new SpeechSynthesisUtterance(currentCard.word);
    utterance.lang = 'en-US';
    utterance.rate = 0.8;
    window.speechSynthesis.speak(utterance);
  };

  const handleComplete = async () => {
    const total = Math.max(cards.length, 1);
    const score = Math.round((known.length / total) * 100);
    if (lessonId) {
      await completeAndSave(String(lessonId), score);
    } else {
      toast.success('Đã hoàn thành flashcard!');
    }
    navigate('/student/learning');
  };

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-3 bg-gradient-to-br from-orange-50 to-yellow-50 dark:from-gray-900 dark:to-gray-800">
        <Loader2 className="h-10 w-10 animate-spin text-orange-500" />
        <p className="text-sm text-muted-foreground">Đang tải từ vựng...</p>
      </div>
    );
  }

  if (!lessonId) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4 p-6">
        <p className="text-muted-foreground">Thiếu mã bài học.</p>
        <Button onClick={() => navigate('/student/learning')}>Quay lại</Button>
      </div>
    );
  }

  if (cards.length === 0) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-orange-50 to-yellow-50 dark:from-gray-900 dark:to-gray-800 p-6">
        <div className="max-w-lg mx-auto space-y-4 text-center pt-16">
          <p className="text-lg font-medium">{lessonTitle || 'Flashcard'}</p>
          <p className="text-sm text-muted-foreground">
            Chưa có từ vựng trong bài này. Hãy báo giáo viên/admin thêm từ (bài loại VOCAB → lưu danh sách từ).
          </p>
          <Button onClick={() => navigate('/student/learning')} className="gap-2">
            <ArrowLeft className="w-4 h-4" />
            Quay lại
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 to-yellow-50 dark:from-gray-900 dark:to-gray-800 p-6">
      <div className="max-w-3xl mx-auto space-y-6">
        <div className="flex items-center justify-between gap-2 flex-wrap">
          <div className="flex flex-col gap-1">
            <Button
              variant="ghost"
              onClick={() => navigate('/student/learning')}
              className="gap-2 w-fit"
            >
              <ArrowLeft className="w-4 h-4" />
              Back
            </Button>
            {lessonTitle && (
              <p className="text-sm text-muted-foreground pl-1 max-w-xl truncate">{lessonTitle}</p>
            )}
          </div>
          <div className="flex items-center gap-4">
            <Badge variant="secondary" className="gap-1">
              <CheckCircle2 className="w-4 h-4 text-green-600" />
              {known.length} Known
            </Badge>
            <Badge variant="outline" className="gap-1">
              <Star className="w-4 h-4 text-yellow-600" />
              {learning.length} Learning
            </Badge>
          </div>
        </div>

        <div className="space-y-2">
          <div className="flex justify-between text-sm text-gray-600 dark:text-gray-400">
            <span>Card {currentIndex + 1} of {cards.length}</span>
            <span>{Math.round(progress)}%</span>
          </div>
          <Progress value={progress} className="h-2" />
        </div>

        <div className="perspective-1000">
          <AnimatePresence mode="wait">
            <motion.div
              key={currentIndex}
              initial={{ rotateY: 90, opacity: 0 }}
              animate={{ rotateY: 0, opacity: 1 }}
              exit={{ rotateY: -90, opacity: 0 }}
              transition={{ duration: 0.3 }}
            >
              <Card
                className="h-[500px] cursor-pointer relative overflow-hidden"
                onClick={handleFlip}
              >
                <motion.div
                  className="w-full h-full"
                  animate={{ rotateY: isFlipped ? 180 : 0 }}
                  transition={{ duration: 0.6 }}
                  style={{ transformStyle: 'preserve-3d' }}
                >
                  <div
                    className="absolute inset-0 p-8 flex flex-col items-center justify-center"
                    style={{
                      backfaceVisibility: 'hidden',
                    }}
                  >
                    {currentCard.image && (
                      <img
                        src={currentCard.image}
                        alt={currentCard.word}
                        className="w-48 h-48 object-cover rounded-lg mb-6"
                      />
                    )}
                    <h1 className="text-4xl sm:text-6xl font-bold text-gray-900 dark:text-white mb-4 text-center break-words px-2">
                      {currentCard.word}
                    </h1>
                    {currentCard.phonetic ? (
                      <p className="text-xl sm:text-2xl text-gray-600 dark:text-gray-400 mb-6">
                        {currentCard.phonetic}
                      </p>
                    ) : null}
                    <Button
                      onClick={(e) => {
                        e.stopPropagation();
                        playAudio();
                      }}
                      variant="outline"
                      size="lg"
                      className="gap-2"
                    >
                      <Volume2 className="w-5 h-5" />
                      Listen
                    </Button>
                    <p className="text-sm text-gray-500 dark:text-gray-400 absolute bottom-8">
                      Click to flip
                    </p>
                  </div>

                  <div
                    className="absolute inset-0 p-8 flex flex-col items-center justify-center bg-gradient-to-br from-green-50 to-blue-50 dark:from-green-900/20 dark:to-blue-900/20"
                    style={{
                      backfaceVisibility: 'hidden',
                      transform: 'rotateY(180deg)',
                    }}
                  >
                    <h2 className="text-3xl sm:text-5xl font-bold text-green-700 dark:text-green-400 mb-8 text-center px-2 break-words">
                      {currentCard.translation}
                    </h2>
                    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-md">
                      <p className="text-lg text-gray-900 dark:text-white mb-2">
                        {currentCard.example}
                      </p>
                      {currentCard.exampleTranslation ? (
                        <p className="text-gray-600 dark:text-gray-400">
                          {currentCard.exampleTranslation}
                        </p>
                      ) : null}
                    </div>
                    <p className="text-sm text-gray-500 dark:text-gray-400 absolute bottom-8">
                      Click to flip back
                    </p>
                  </div>
                </motion.div>
              </Card>
            </motion.div>
          </AnimatePresence>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <Button
            onClick={handleLearning}
            variant="outline"
            size="lg"
            className="gap-2 border-2 border-yellow-500 hover:bg-yellow-50 dark:hover:bg-yellow-900/20"
          >
            <Star className="w-5 h-5 text-yellow-600" />
            Still Learning
          </Button>
          <Button
            onClick={handleKnown}
            size="lg"
            className="gap-2 bg-green-600 hover:bg-green-700 text-white"
          >
            <CheckCircle2 className="w-5 h-5" />
            I Know This
          </Button>
        </div>

        <div className="flex justify-between items-center">
          <Button
            variant="outline"
            onClick={handlePrevious}
            disabled={currentIndex === 0}
            className="gap-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Previous
          </Button>

          <Button onClick={handleFlip} variant="ghost" className="gap-2">
            <RotateCcw className="w-4 h-4" />
            Flip Card
          </Button>

          {currentIndex === cards.length - 1 ? (
            <Button
              onClick={handleComplete}
              className="bg-green-600 hover:bg-green-700 text-white gap-2"
            >
              <CheckCircle2 className="w-5 h-5" />
              Complete
            </Button>
          ) : (
            <Button
              onClick={handleNext}
              className="gap-2"
            >
              Next
              <ArrowRight className="w-4 h-4" />
            </Button>
          )}
        </div>

        <Card className="p-6 bg-gradient-to-r from-green-50 to-blue-50 dark:from-green-900/20 dark:to-blue-900/20">
          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-3xl font-bold text-gray-900 dark:text-white">
                {cards.length}
              </p>
              <p className="text-sm text-gray-600 dark:text-gray-400">Total Cards</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-green-600 dark:text-green-400">
                {known.length}
              </p>
              <p className="text-sm text-gray-600 dark:text-gray-400">Known</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-yellow-600 dark:text-yellow-400">
                {learning.length}
              </p>
              <p className="text-sm text-gray-600 dark:text-gray-400">Learning</p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}

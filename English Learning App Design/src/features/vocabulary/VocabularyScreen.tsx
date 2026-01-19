import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { useAppStore } from '@/store/appStore';
import { mockVocabulary } from '@/services/mockData';
import { ArrowLeft, Volume2 } from 'lucide-react';
import { motion } from 'motion/react';
import { useState } from 'react';

export function VocabularyScreen() {
  const { setCurrentView } = useAppStore();
  const [flippedCards, setFlippedCards] = useState<Set<string>>(new Set());

  const toggleFlip = (wordId: string) => {
    const newFlipped = new Set(flippedCards);
    if (newFlipped.has(wordId)) {
      newFlipped.delete(wordId);
    } else {
      newFlipped.add(wordId);
    }
    setFlippedCards(newFlipped);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <Button
        variant="ghost"
        size="sm"
        className="mb-4"
        onClick={() => setCurrentView('home')}
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Quay lại
      </Button>

      <div className="mb-8">
        <h1 className="mb-2 text-3xl font-bold">Từ vựng của tôi</h1>
        <p className="text-gray-600 dark:text-gray-400">
          Ôn tập các từ vựng bạn đã học
        </p>
      </div>

      {/* Vocabulary Grid */}
      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {mockVocabulary.map((word) => {
          const isFlipped = flippedCards.has(word.id);
          
          return (
            <motion.div
              key={word.id}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              style={{ perspective: 1000 }}
            >
              <Card
                className="relative h-64 cursor-pointer"
                onClick={() => toggleFlip(word.id)}
              >
                <motion.div
                  className="h-full w-full"
                  animate={{ rotateY: isFlipped ? 180 : 0 }}
                  transition={{ duration: 0.6 }}
                  style={{
                    transformStyle: 'preserve-3d',
                  }}
                >
                  {/* Front Side */}
                  <div
                    className="absolute inset-0 flex flex-col items-center justify-center p-6"
                    style={{
                      backfaceVisibility: 'hidden',
                    }}
                  >
                    <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-blue-100 dark:bg-blue-900">
                      <span className="text-3xl">📖</span>
                    </div>
                    <h3 className="mb-2 text-center text-2xl font-bold">{word.word}</h3>
                    <p className="mb-4 text-center text-sm text-gray-500">{word.pronunciation}</p>
                    <Button variant="ghost" size="sm">
                      <Volume2 className="h-4 w-4" />
                    </Button>
                    <p className="mt-4 text-center text-xs text-gray-400">Click để xem nghĩa</p>
                  </div>

                  {/* Back Side */}
                  <div
                    className="absolute inset-0 flex flex-col items-center justify-center bg-gradient-to-br from-purple-500 to-blue-500 p-6 text-white"
                    style={{
                      backfaceVisibility: 'hidden',
                      transform: 'rotateY(180deg)',
                    }}
                  >
                    <h3 className="mb-4 text-center text-2xl font-bold">{word.meaning}</h3>
                    <p className="mb-4 text-center italic opacity-90">"{word.example}"</p>
                    {word.learned && (
                      <div className="mt-4 rounded-full bg-white/20 px-3 py-1 text-sm">
                        ✓ Đã học
                      </div>
                    )}
                  </div>
                </motion.div>
              </Card>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}

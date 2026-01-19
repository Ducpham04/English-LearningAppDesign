import { Card } from '@/app/components/ui/card';
import { ProgressBar } from '@/shared/ui/ProgressBar';
import { LevelBadge } from '@/shared/ui/LevelBadge';
import { Clock, BookOpen, Lock, CheckCircle } from 'lucide-react';
import type { Session } from '@/types';
import { motion } from 'motion/react';

interface SessionCardProps {
  session: Session;
  onClick?: () => void;
}

export function SessionCard({ session, onClick }: SessionCardProps) {
  const progress = (session.completedLessons / session.totalLessons) * 100;
  const isLocked = session.status === 'locked';

  return (
    <motion.div
      whileHover={{ scale: isLocked ? 1 : 1.03, y: isLocked ? 0 : -4 }}
      whileTap={{ scale: isLocked ? 1 : 0.98 }}
      transition={{ duration: 0.2 }}
    >
      <Card
        className={`relative cursor-pointer overflow-hidden transition-all hover:shadow-xl rounded-3xl border-2 ${
          isLocked 
            ? 'opacity-60 cursor-not-allowed border-gray-200 dark:border-gray-700' 
            : 'border-green-100 dark:border-green-900 hover:border-green-300 dark:hover:border-green-700'
        }`}
        onClick={() => !isLocked && onClick?.()}
      >
        {/* Thumbnail */}
        <div className="relative h-40 w-full overflow-hidden bg-gradient-to-br from-green-400 to-green-500 rounded-t-3xl">
          {session.thumbnail && (
            <img
              src={session.thumbnail}
              alt={session.title}
              className="h-full w-full object-cover"
            />
          )}
          {isLocked && (
            <div className="absolute inset-0 flex items-center justify-center bg-black/50 backdrop-blur-sm">
              <div className="rounded-2xl bg-gray-800/80 p-4">
                <Lock className="h-12 w-12 text-white" />
              </div>
            </div>
          )}
          {session.status === 'completed' && (
            <div className="absolute right-3 top-3 rounded-2xl bg-green-500 p-2 shadow-lg">
              <CheckCircle className="h-6 w-6 text-white" />
            </div>
          )}
          <div className="absolute left-3 top-3">
            <LevelBadge level={session.level} />
          </div>
        </div>

        {/* Content */}
        <div className="p-5">
          <h3 className="mb-2 line-clamp-1 font-bold text-lg">
            {session.title}
          </h3>
          <p className="mb-4 line-clamp-2 text-sm text-gray-600 dark:text-gray-400">
            {session.description}
          </p>

          {/* Stats */}
          <div className="mb-3 flex items-center gap-3 text-sm text-gray-600 dark:text-gray-400">
            <div className="flex items-center gap-1.5 bg-green-50 dark:bg-green-900/20 rounded-xl px-3 py-1.5">
              <BookOpen className="h-4 w-4 text-green-600 dark:text-green-400" />
              <span className="font-medium">{session.totalLessons} bài</span>
            </div>
            <div className="flex items-center gap-1.5 bg-green-50 dark:bg-green-900/20 rounded-xl px-3 py-1.5">
              <Clock className="h-4 w-4 text-green-600 dark:text-green-400" />
              <span className="font-medium">{session.estimatedTime}p</span>
            </div>
          </div>

          {/* Progress */}
          {!isLocked && (
            <ProgressBar value={progress} showLabel />
          )}
        </div>
      </Card>
    </motion.div>
  );
}
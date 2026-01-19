import { cn } from '@/app/components/ui/utils';

interface ProgressBarProps {
  value: number; // 0-100
  className?: string;
  showLabel?: boolean;
}

export function ProgressBar({ value, className, showLabel = false }: ProgressBarProps) {
  const percentage = Math.min(100, Math.max(0, value));

  return (
    <div className={cn('w-full', className)}>
      <div className="relative h-2.5 w-full overflow-hidden rounded-full bg-gray-200 dark:bg-gray-700">
        <div
          className="h-full bg-gradient-to-r from-green-400 to-green-600 transition-all duration-300 ease-out shadow-sm"
          style={{ width: `${percentage}%` }}
        />
      </div>
      {showLabel && (
        <p className="mt-1.5 text-xs font-medium text-green-600 dark:text-green-400">
          {percentage}% hoàn thành
        </p>
      )}
    </div>
  );
}
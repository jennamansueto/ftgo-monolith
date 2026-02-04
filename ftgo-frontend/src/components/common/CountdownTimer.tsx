import { Clock, AlertTriangle } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useCountdown } from '@/features/restaurant/hooks/useCountdown';

interface CountdownTimerProps {
  targetTime: string | null | undefined;
  label?: string;
  showIcon?: boolean;
  className?: string;
}

export function CountdownTimer({
  targetTime,
  label,
  showIcon = true,
  className,
}: CountdownTimerProps) {
  const { formattedTime, isOverdue, isActive } = useCountdown(targetTime);

  if (!isActive) {
    return null;
  }

  return (
    <div
      className={cn(
        'flex items-center gap-1.5 text-sm font-medium',
        isOverdue ? 'text-danger' : 'text-gray-700',
        className
      )}
    >
      {showIcon && (
        isOverdue ? (
          <AlertTriangle className="w-4 h-4" />
        ) : (
          <Clock className="w-4 h-4" />
        )
      )}
      {label && <span className="text-gray-500">{label}</span>}
      <span className={cn(isOverdue && 'font-bold')}>
        {isOverdue ? `-${formattedTime}` : formattedTime}
      </span>
      {isOverdue && <span className="text-xs">(overdue)</span>}
    </div>
  );
}

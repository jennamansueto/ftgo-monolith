import { useState, useEffect, useCallback } from 'react';

interface CountdownResult {
  timeRemaining: number;
  formattedTime: string;
  isOverdue: boolean;
  isActive: boolean;
}

export function useCountdown(targetTime: string | null | undefined): CountdownResult {
  const [timeRemaining, setTimeRemaining] = useState<number>(0);

  const calculateTimeRemaining = useCallback(() => {
    if (!targetTime) return 0;
    const target = new Date(targetTime).getTime();
    const now = Date.now();
    return Math.floor((target - now) / 1000);
  }, [targetTime]);

  useEffect(() => {
    if (!targetTime) {
      setTimeRemaining(0);
      return;
    }

    setTimeRemaining(calculateTimeRemaining());

    const interval = setInterval(() => {
      setTimeRemaining(calculateTimeRemaining());
    }, 1000);

    return () => clearInterval(interval);
  }, [targetTime, calculateTimeRemaining]);

  const formatTime = (seconds: number): string => {
    const absSeconds = Math.abs(seconds);
    const hours = Math.floor(absSeconds / 3600);
    const minutes = Math.floor((absSeconds % 3600) / 60);
    const secs = absSeconds % 60;

    if (hours > 0) {
      return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  const isOverdue = timeRemaining < 0;
  const isActive = targetTime !== null && targetTime !== undefined;

  return {
    timeRemaining,
    formattedTime: formatTime(timeRemaining),
    isOverdue,
    isActive,
  };
}

import { useState, useEffect, useCallback } from 'react';

interface CountdownResult {
  timeRemaining: number;
  isExpired: boolean;
  formattedTime: string;
}

export function useCountdown(targetTime: string | null | undefined): CountdownResult {
  const calculateTimeRemaining = useCallback(() => {
    if (!targetTime) return 0;
    const target = new Date(targetTime).getTime();
    const now = Date.now();
    return Math.max(0, Math.floor((target - now) / 1000));
  }, [targetTime]);

  const [timeRemaining, setTimeRemaining] = useState<number>(calculateTimeRemaining);

  useEffect(() => {
    setTimeRemaining(calculateTimeRemaining());

    const interval = setInterval(() => {
      setTimeRemaining(calculateTimeRemaining());
    }, 1000);

    return () => clearInterval(interval);
  }, [calculateTimeRemaining]);

  const formatTime = (seconds: number): string => {
    if (seconds <= 0) return '0:00';
    const hours = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    if (hours > 0) {
      return `${hours}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return {
    timeRemaining,
    isExpired: timeRemaining <= 0,
    formattedTime: formatTime(timeRemaining),
  };
}

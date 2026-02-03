import { cn } from '@/lib/utils';
import { moneyUtils, type Money } from '@/types';

interface MoneyDisplayProps {
  amount: Money;
  className?: string;
}

export function MoneyDisplay({ amount, className }: MoneyDisplayProps) {
  return (
    <span className={cn(className)}>
      {moneyUtils.format(amount)}
    </span>
  );
}

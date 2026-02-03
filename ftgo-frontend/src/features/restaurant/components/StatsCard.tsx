import { cn } from '@/lib/utils';

interface StatsCardProps {
  title: string;
  value: number;
  color: 'primary' | 'warning' | 'success' | 'gray';
}

const colorClasses = {
  primary: 'border-l-primary',
  warning: 'border-l-warning',
  success: 'border-l-success',
  gray: 'border-l-gray-400',
};

const valueColorClasses = {
  primary: 'text-primary',
  warning: 'text-warning',
  success: 'text-success',
  gray: 'text-secondary',
};

export function StatsCard({ title, value, color }: StatsCardProps) {
  return (
    <div
      className={cn(
        'flex-1 bg-white rounded-xl p-4 shadow-sm border-l-4',
        colorClasses[color]
      )}
    >
      <p className="text-sm text-gray-500">{title}</p>
      <p className={cn('text-3xl font-bold', valueColorClasses[color])}>{value}</p>
    </div>
  );
}

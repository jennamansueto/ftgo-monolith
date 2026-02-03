import { cn } from '@/lib/utils';
import type { OrderState } from '@/types';

interface OrderStatusBadgeProps {
  state: OrderState;
  className?: string;
}

const stateConfig: Record<OrderState, { label: string; className: string }> = {
  APPROVED: {
    label: 'New',
    className: 'bg-primary/10 text-primary',
  },
  ACCEPTED: {
    label: 'Accepted',
    className: 'bg-blue-100 text-blue-700',
  },
  PREPARING: {
    label: 'Preparing',
    className: 'bg-warning/10 text-warning',
  },
  READY_FOR_PICKUP: {
    label: 'Ready',
    className: 'bg-success/10 text-success',
  },
  PICKED_UP: {
    label: 'Picked Up',
    className: 'bg-purple-100 text-purple-700',
  },
  DELIVERED: {
    label: 'Delivered',
    className: 'bg-gray-100 text-gray-600',
  },
  CANCELLED: {
    label: 'Cancelled',
    className: 'bg-danger/10 text-danger',
  },
};

export function OrderStatusBadge({ state, className }: OrderStatusBadgeProps) {
  const config = stateConfig[state];
  
  return (
    <span
      className={cn(
        'inline-flex items-center px-2 py-1 rounded text-xs font-medium',
        config.className,
        className
      )}
    >
      {config.label}
    </span>
  );
}

import { Check, Clock, ChefHat, Package, Truck, MapPin } from 'lucide-react';
import { cn } from '@/lib/utils';
import type { OrderState } from '@/types';

interface TimelineStep {
  label: string;
  time?: string | null;
  estimatedTime?: string | null;
  icon: React.ReactNode;
  state: OrderState[];
}

interface DeliveryTimelineProps {
  currentState: OrderState;
  acceptTime?: string | null;
  preparingTime?: string | null;
  readyForPickupTime?: string | null;
  pickedUpTime?: string | null;
  deliveredTime?: string | null;
  readyBy?: string | null;
  estimatedPickupTime?: string | null;
  estimatedDeliveryTime?: string | null;
  assignedCourierId?: number | null;
  className?: string;
}

const formatDateTime = (dateString: string | null | undefined): string => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleTimeString('en-US', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true,
  });
};

const getStepStatus = (
  currentState: OrderState,
  stepStates: OrderState[]
): 'completed' | 'current' | 'pending' => {
  const stateOrder: OrderState[] = [
    'APPROVED',
    'ACCEPTED',
    'PREPARING',
    'READY_FOR_PICKUP',
    'PICKED_UP',
    'DELIVERED',
  ];

  const currentIndex = stateOrder.indexOf(currentState);
  const stepIndex = Math.max(...stepStates.map((s) => stateOrder.indexOf(s)));

  if (currentState === 'CANCELLED') {
    return 'pending';
  }

  if (stepStates.includes(currentState)) {
    return 'current';
  }

  if (currentIndex > stepIndex) {
    return 'completed';
  }

  return 'pending';
};

export function DeliveryTimeline({
  currentState,
  acceptTime,
  preparingTime,
  readyForPickupTime,
  pickedUpTime,
  deliveredTime,
  readyBy,
  estimatedPickupTime,
  estimatedDeliveryTime,
  assignedCourierId,
  className,
}: DeliveryTimelineProps) {
  const steps: TimelineStep[] = [
    {
      label: 'Order Accepted',
      time: acceptTime,
      icon: <Check className="w-4 h-4" />,
      state: ['ACCEPTED'],
    },
    {
      label: 'Preparing',
      time: preparingTime,
      estimatedTime: readyBy,
      icon: <ChefHat className="w-4 h-4" />,
      state: ['PREPARING'],
    },
    {
      label: 'Ready for Pickup',
      time: readyForPickupTime,
      estimatedTime: estimatedPickupTime,
      icon: <Package className="w-4 h-4" />,
      state: ['READY_FOR_PICKUP'],
    },
    {
      label: 'Picked Up',
      time: pickedUpTime,
      icon: <Truck className="w-4 h-4" />,
      state: ['PICKED_UP'],
    },
    {
      label: 'Delivered',
      time: deliveredTime,
      estimatedTime: estimatedDeliveryTime,
      icon: <MapPin className="w-4 h-4" />,
      state: ['DELIVERED'],
    },
  ];

  return (
    <div className={cn('space-y-1', className)}>
      {assignedCourierId && (
        <div className="text-xs text-gray-500 mb-2">
          Courier #{assignedCourierId} assigned
        </div>
      )}
      <div className="relative">
        {steps.map((step, index) => {
          const status = getStepStatus(currentState, step.state);
          const isLast = index === steps.length - 1;

          return (
            <div key={step.label} className="flex items-start gap-3 pb-4 last:pb-0">
              <div className="relative flex flex-col items-center">
                <div
                  className={cn(
                    'w-8 h-8 rounded-full flex items-center justify-center border-2 transition-colors',
                    status === 'completed' && 'bg-success border-success text-white',
                    status === 'current' && 'bg-primary border-primary text-white',
                    status === 'pending' && 'bg-gray-100 border-gray-300 text-gray-400'
                  )}
                >
                  {step.icon}
                </div>
                {!isLast && (
                  <div
                    className={cn(
                      'w-0.5 h-full absolute top-8 left-1/2 -translate-x-1/2',
                      status === 'completed' ? 'bg-success' : 'bg-gray-200'
                    )}
                    style={{ minHeight: '24px' }}
                  />
                )}
              </div>
              <div className="flex-1 min-w-0 pt-1">
                <p
                  className={cn(
                    'text-sm font-medium',
                    status === 'pending' ? 'text-gray-400' : 'text-gray-900'
                  )}
                >
                  {step.label}
                </p>
                <div className="flex items-center gap-2 text-xs">
                  {step.time ? (
                    <span className="text-gray-600">{formatDateTime(step.time)}</span>
                  ) : step.estimatedTime ? (
                    <span className="text-gray-400 flex items-center gap-1">
                      <Clock className="w-3 h-3" />
                      ETA: {formatDateTime(step.estimatedTime)}
                    </span>
                  ) : null}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

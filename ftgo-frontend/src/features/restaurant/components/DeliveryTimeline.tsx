import { CheckCircle, Circle, Clock } from 'lucide-react';
import { cn } from '@/lib/utils';
import type { OrderState } from '@/types';

interface TimelineStep {
  label: string;
  state: OrderState;
  time?: string | null;
  estimatedTime?: string | null;
}

interface DeliveryTimelineProps {
  currentState: OrderState;
  acceptTime?: string | null;
  preparingTime?: string | null;
  readyForPickupTime?: string | null;
  pickedUpTime?: string | null;
  deliveredTime?: string | null;
  estimatedPickupTime?: string | null;
  estimatedDeliveryTime?: string | null;
  readyBy?: string | null;
}

const stateOrder: OrderState[] = [
  'APPROVED',
  'ACCEPTED',
  'PREPARING',
  'READY_FOR_PICKUP',
  'PICKED_UP',
  'DELIVERED',
];

export function DeliveryTimeline({
  currentState,
  acceptTime,
  preparingTime,
  readyForPickupTime,
  pickedUpTime,
  deliveredTime,
  estimatedPickupTime,
  estimatedDeliveryTime,
  readyBy,
}: DeliveryTimelineProps) {
  const currentIndex = stateOrder.indexOf(currentState);

  const steps: TimelineStep[] = [
    { label: 'Order Placed', state: 'APPROVED', time: null, estimatedTime: null },
    { label: 'Accepted', state: 'ACCEPTED', time: acceptTime, estimatedTime: null },
    { label: 'Preparing', state: 'PREPARING', time: preparingTime, estimatedTime: readyBy },
    { label: 'Ready for Pickup', state: 'READY_FOR_PICKUP', time: readyForPickupTime, estimatedTime: estimatedPickupTime },
    { label: 'Picked Up', state: 'PICKED_UP', time: pickedUpTime, estimatedTime: null },
    { label: 'Delivered', state: 'DELIVERED', time: deliveredTime, estimatedTime: estimatedDeliveryTime },
  ];

  const formatTime = (timeStr: string | null | undefined): string => {
    if (!timeStr) return '';
    return new Date(timeStr).toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit',
    });
  };

  if (currentState === 'CANCELLED') {
    return (
      <div className="py-4 text-center text-danger">
        <p className="font-medium">Order Cancelled</p>
      </div>
    );
  }

  return (
    <div className="py-4">
      <div className="relative">
        {steps.map((step, index) => {
          const stepIndex = stateOrder.indexOf(step.state);
          const isCompleted = stepIndex >= 0 && stepIndex < currentIndex;
          const isCurrent = step.state === currentState;
          const isPending = stepIndex > currentIndex;

          return (
            <div key={step.label} className="flex items-start mb-4 last:mb-0">
              <div className="flex flex-col items-center mr-4">
                {isCompleted ? (
                  <CheckCircle className="w-6 h-6 text-success" />
                ) : isCurrent ? (
                  <Clock className="w-6 h-6 text-primary animate-pulse" />
                ) : (
                  <Circle className="w-6 h-6 text-gray-300" />
                )}
                {index < steps.length - 1 && (
                  <div
                    className={cn(
                      'w-0.5 h-8 mt-1',
                      isCompleted ? 'bg-success' : 'bg-gray-200'
                    )}
                  />
                )}
              </div>
              <div className="flex-1">
                <p
                  className={cn(
                    'font-medium',
                    isCompleted && 'text-success',
                    isCurrent && 'text-primary',
                    isPending && 'text-gray-400'
                  )}
                >
                  {step.label}
                </p>
                {step.time && (
                  <p className="text-sm text-gray-500">
                    Completed at {formatTime(step.time)}
                  </p>
                )}
                {!step.time && step.estimatedTime && (
                  <p className="text-sm text-gray-400">
                    Est. {formatTime(step.estimatedTime)}
                  </p>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

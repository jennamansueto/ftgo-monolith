import { useState } from 'react';
import { Clock, ChefHat, CheckCircle, XCircle } from 'lucide-react';
import { cn } from '@/lib/utils';
import { OrderStatusBadge, MoneyDisplay } from '@/components/common';
import { SendMessageForm } from './SendMessageForm';
import type { Order } from '@/types';

interface OrderCardProps {
  order: Order;
  onAccept?: (orderId: number, readyBy: string) => void;
  onPreparing?: (orderId: number) => void;
  onReady?: (orderId: number) => void;
  onCancel?: (orderId: number) => void;
  onSendMessage?: (orderId: number, message: string) => void;
  isLoading?: boolean;
  isMessageLoading?: boolean;
}

export function OrderCard({
  order,
  onAccept,
  onPreparing,
  onReady,
  onCancel,
  onSendMessage,
  isLoading,
  isMessageLoading,
}: OrderCardProps) {
  const [readyInMinutes, setReadyInMinutes] = useState(20);

  const handleAccept = () => {
    if (onAccept) {
      const readyBy = new Date(Date.now() + readyInMinutes * 60000).toISOString();
      onAccept(order.orderId, readyBy);
    }
  };

  const getBorderColor = () => {
    switch (order.state) {
      case 'APPROVED':
        return 'border-primary';
      case 'ACCEPTED':
        return 'border-blue-500';
      case 'PREPARING':
        return 'border-warning';
      case 'READY_FOR_PICKUP':
        return 'border-success';
      default:
        return 'border-gray-200';
    }
  };

  return (
    <div
      className={cn(
        'bg-white rounded-xl p-6 shadow-sm border-2 transition-all',
        getBorderColor(),
        isLoading && 'opacity-50 pointer-events-none'
      )}
    >
      <div className="flex justify-between items-start mb-4">
        <div>
          <OrderStatusBadge state={order.state} />
          <h3 className="text-lg font-bold text-secondary mt-2">Order #{order.orderId}</h3>
          {order.restaurantName && (
            <p className="text-sm text-gray-500">{order.restaurantName}</p>
          )}
        </div>
        <MoneyDisplay amount={order.orderTotal} className="text-xl font-bold text-primary" />
      </div>

      {order.lineItems && order.lineItems.length > 0 && (
        <div className="border-t pt-4 mb-4">
          {order.lineItems.map((item, index) => (
            <p key={index} className="text-sm text-gray-600 mb-1">
              {item.quantity}x {item.name}
            </p>
          ))}
        </div>
      )}

      <div className="flex gap-2 flex-wrap">
        {order.state === 'APPROVED' && onAccept && (
          <>
            <div className="flex items-center gap-2 mr-2">
              <label className="text-sm text-gray-600">Ready in:</label>
              <select
                value={readyInMinutes}
                onChange={(e) => setReadyInMinutes(Number(e.target.value))}
                className="border rounded px-2 py-1 text-sm"
              >
                <option value={10}>10 min</option>
                <option value={15}>15 min</option>
                <option value={20}>20 min</option>
                <option value={30}>30 min</option>
                <option value={45}>45 min</option>
                <option value={60}>60 min</option>
              </select>
            </div>
            <button
              onClick={handleAccept}
              disabled={isLoading}
              className="flex-1 bg-primary text-white py-2 px-4 rounded-lg font-medium hover:bg-orange-600 transition flex items-center justify-center gap-2"
            >
              <CheckCircle className="w-4 h-4" />
              Accept Order
            </button>
          </>
        )}

        {order.state === 'ACCEPTED' && onPreparing && (
          <button
            onClick={() => onPreparing(order.orderId)}
            disabled={isLoading}
            className="flex-1 bg-warning text-white py-2 px-4 rounded-lg font-medium hover:bg-amber-600 transition flex items-center justify-center gap-2"
          >
            <ChefHat className="w-4 h-4" />
            Start Preparing
          </button>
        )}

        {order.state === 'PREPARING' && onReady && (
          <button
            onClick={() => onReady(order.orderId)}
            disabled={isLoading}
            className="flex-1 bg-success text-white py-2 px-4 rounded-lg font-medium hover:bg-green-600 transition flex items-center justify-center gap-2"
          >
            <Clock className="w-4 h-4" />
            Mark Ready
          </button>
        )}

        {(order.state === 'APPROVED' || order.state === 'ACCEPTED') && onCancel && (
          <button
            onClick={() => onCancel(order.orderId)}
            disabled={isLoading}
            className="px-4 py-2 border border-danger text-danger rounded-lg font-medium hover:bg-danger/5 transition flex items-center justify-center gap-2"
          >
            <XCircle className="w-4 h-4" />
            Cancel
          </button>
        )}
      </div>

      {onSendMessage && (
        <SendMessageForm
          orderId={order.orderId}
          onSend={onSendMessage}
          isLoading={isMessageLoading}
        />
      )}
    </div>
  );
}

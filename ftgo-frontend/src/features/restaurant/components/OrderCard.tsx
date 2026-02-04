import { useState } from 'react';
import { Clock, ChefHat, CheckCircle, XCircle, Timer, Truck, Edit2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import { OrderStatusBadge, MoneyDisplay } from '@/components/common';
import { useCountdown } from '../hooks/useCountdown';
import type { Order } from '@/types';

interface OrderCardProps {
  order: Order;
  onAccept?: (orderId: number, readyBy: string) => void;
  onPreparing?: (orderId: number) => void;
  onReady?: (orderId: number) => void;
  onCancel?: (orderId: number) => void;
  onUpdateEta?: (orderId: number, estimatedPickupTime?: string, estimatedDeliveryTime?: string) => void;
  isLoading?: boolean;
}

export function OrderCard({
  order,
  onAccept,
  onPreparing,
  onReady,
  onCancel,
  onUpdateEta,
  isLoading,
}: OrderCardProps) {
  const [readyInMinutes, setReadyInMinutes] = useState(20);
  const [isEditingEta, setIsEditingEta] = useState(false);
  const [newPrepMinutes, setNewPrepMinutes] = useState(20);

  const prepCountdown = useCountdown(order.readyBy);
  const deliveryCountdown = useCountdown(order.estimatedDeliveryTime);

  const handleAccept = () => {
    if (onAccept) {
      const readyBy = new Date(Date.now() + readyInMinutes * 60000).toISOString();
      onAccept(order.orderId, readyBy);
    }
  };

  const handleUpdatePrepTime = () => {
    if (onUpdateEta) {
      const newReadyBy = new Date(Date.now() + newPrepMinutes * 60000).toISOString();
      const newDeliveryTime = new Date(Date.now() + (newPrepMinutes + 30) * 60000).toISOString();
      onUpdateEta(order.orderId, newReadyBy, newDeliveryTime);
      setIsEditingEta(false);
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

      {(['ACCEPTED', 'PREPARING'].includes(order.state) && order.readyBy) || 
       (['READY_FOR_PICKUP', 'PICKED_UP'].includes(order.state) && order.estimatedDeliveryTime) ? (
        <div className="bg-gray-50 rounded-lg p-3 mb-4 space-y-2">
          {['ACCEPTED', 'PREPARING'].includes(order.state) && order.readyBy && (
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Timer className="w-4 h-4 text-warning" />
                <span className="text-sm text-gray-600">Prep Time Remaining:</span>
              </div>
              <span className={cn(
                "font-mono font-bold",
                prepCountdown.isExpired ? "text-danger" : "text-warning"
              )}>
                {prepCountdown.isExpired ? "OVERDUE" : prepCountdown.formattedTime}
              </span>
            </div>
          )}
          {['READY_FOR_PICKUP', 'PICKED_UP'].includes(order.state) && order.estimatedDeliveryTime && (
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Truck className="w-4 h-4 text-primary" />
                <span className="text-sm text-gray-600">Est. Delivery:</span>
              </div>
              <span className={cn(
                "font-mono font-bold",
                deliveryCountdown.isExpired ? "text-danger" : "text-primary"
              )}>
                {deliveryCountdown.isExpired ? "OVERDUE" : deliveryCountdown.formattedTime}
              </span>
            </div>
          )}
        </div>
      ) : null}

      {order.assignedCourier && (
        <div className="flex items-center gap-2 text-sm text-gray-600 mb-4">
          <Truck className="w-4 h-4" />
          <span>Courier #{order.assignedCourier} assigned</span>
        </div>
      )}

      {order.state === 'ACCEPTED' && onUpdateEta && (
        <div className="mb-4">
          {isEditingEta ? (
            <div className="flex items-center gap-2">
              <select
                value={newPrepMinutes}
                onChange={(e) => setNewPrepMinutes(Number(e.target.value))}
                className="border rounded px-2 py-1 text-sm"
              >
                <option value={10}>10 min</option>
                <option value={15}>15 min</option>
                <option value={20}>20 min</option>
                <option value={30}>30 min</option>
                <option value={45}>45 min</option>
                <option value={60}>60 min</option>
              </select>
              <button
                onClick={handleUpdatePrepTime}
                className="px-3 py-1 bg-primary text-white rounded text-sm"
              >
                Update
              </button>
              <button
                onClick={() => setIsEditingEta(false)}
                className="px-3 py-1 border rounded text-sm"
              >
                Cancel
              </button>
            </div>
          ) : (
            <button
              onClick={() => setIsEditingEta(true)}
              className="flex items-center gap-1 text-sm text-primary hover:underline"
            >
              <Edit2 className="w-3 h-3" />
              Adjust prep time
            </button>
          )}
        </div>
      )}

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
    </div>
  );
}

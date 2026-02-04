import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, MessageSquare } from 'lucide-react';
import { LoadingSpinner, OrderStatusBadge, MoneyDisplay } from '@/components/common';
import { SendMessageForm, MessageHistory } from '../components';
import { useOrder } from '../hooks/useOrders';
import { useOrderMessages, useSendMessage } from '../hooks/useMessages';

export function OrderDetailsPage() {
  const { orderId } = useParams<{ orderId: string }>();
  const orderIdNum = parseInt(orderId || '0', 10);

  const { data: order, isLoading: orderLoading } = useOrder(orderIdNum);
  const { data: messages = [], isLoading: messagesLoading } = useOrderMessages(orderIdNum);
  const sendMessage = useSendMessage();

  const handleSendMessage = (orderId: number, message: string) => {
    sendMessage.mutate({ orderId, message });
  };

  if (orderLoading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-center">
          <p className="text-xl text-gray-600">Order not found</p>
          <Link to="/" className="text-primary hover:underline mt-4 inline-block">
            Back to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white px-8 py-4 border-b">
        <div className="flex items-center gap-4">
          <Link
            to="/"
            className="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition"
          >
            <ArrowLeft className="w-5 h-5" />
            Back to Dashboard
          </Link>
        </div>
      </header>

      <main className="max-w-4xl mx-auto p-8">
        <div className="bg-white rounded-xl shadow-sm border p-6 mb-6">
          <div className="flex justify-between items-start mb-4">
            <div>
              <OrderStatusBadge state={order.state} />
              <h1 className="text-2xl font-bold text-secondary mt-2">
                Order #{order.orderId}
              </h1>
              {order.restaurantName && (
                <p className="text-gray-500">{order.restaurantName}</p>
              )}
            </div>
            <MoneyDisplay amount={order.orderTotal} className="text-2xl font-bold text-primary" />
          </div>

          {order.lineItems && order.lineItems.length > 0 && (
            <div className="border-t pt-4">
              <h3 className="font-semibold text-gray-700 mb-2">Order Items</h3>
              {order.lineItems.map((item, index) => (
                <div key={index} className="flex justify-between text-sm text-gray-600 mb-1">
                  <span>{item.quantity}x {item.name}</span>
                  <MoneyDisplay amount={item.price} />
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="bg-white rounded-xl shadow-sm border p-6">
          <div className="flex items-center gap-2 mb-4">
            <MessageSquare className="w-5 h-5 text-primary" />
            <h2 className="text-lg font-semibold text-secondary">Customer Messages</h2>
          </div>

          <div className="mb-4">
            <SendMessageForm
              orderId={orderIdNum}
              onSend={handleSendMessage}
              isLoading={sendMessage.isPending}
            />
          </div>

          <div className="border-t pt-4">
            <MessageHistory messages={messages} isLoading={messagesLoading} />
          </div>
        </div>
      </main>
    </div>
  );
}

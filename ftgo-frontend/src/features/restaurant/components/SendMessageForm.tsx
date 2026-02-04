import { useState } from 'react';
import { Send } from 'lucide-react';
import { cn } from '@/lib/utils';

interface SendMessageFormProps {
  orderId: number;
  onSend: (orderId: number, message: string) => void;
  isLoading?: boolean;
}

export function SendMessageForm({ orderId, onSend, isLoading }: SendMessageFormProps) {
  const [message, setMessage] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (message.trim() && !isLoading) {
      onSend(orderId, message.trim());
      setMessage('');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="border-t pt-4 mt-4">
      <label className="block text-sm font-medium text-gray-700 mb-2">
        Send message to consumer
      </label>
      <div className="flex gap-2">
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Enter order update message..."
          disabled={isLoading}
          className={cn(
            'flex-1 border rounded-lg px-3 py-2 text-sm',
            'focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent',
            'placeholder:text-gray-400',
            isLoading && 'opacity-50 cursor-not-allowed'
          )}
        />
        <button
          type="submit"
          disabled={!message.trim() || isLoading}
          className={cn(
            'px-4 py-2 bg-primary text-white rounded-lg font-medium',
            'hover:bg-orange-600 transition',
            'flex items-center gap-2',
            'disabled:opacity-50 disabled:cursor-not-allowed'
          )}
        >
          <Send className="w-4 h-4" />
          Send
        </button>
      </div>
    </form>
  );
}

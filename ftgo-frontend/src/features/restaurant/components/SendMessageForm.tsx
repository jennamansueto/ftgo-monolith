import { useState } from 'react';
import { Send } from 'lucide-react';

interface SendMessageFormProps {
  orderId: number;
  onSend: (orderId: number, message: string) => void;
  isLoading?: boolean;
}

export function SendMessageForm({ orderId, onSend, isLoading }: SendMessageFormProps) {
  const [message, setMessage] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (message.trim()) {
      onSend(orderId, message.trim());
      setMessage('');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex gap-2">
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="Type a message to the customer..."
        className="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
        disabled={isLoading}
      />
      <button
        type="submit"
        disabled={isLoading || !message.trim()}
        className="bg-primary text-white px-4 py-2 rounded-lg font-medium hover:bg-orange-600 transition flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <Send className="w-4 h-4" />
        Send
      </button>
    </form>
  );
}

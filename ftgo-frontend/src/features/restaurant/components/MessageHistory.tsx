import { MessageSquare } from 'lucide-react';
import type { OrderMessage } from '@/types';

interface MessageHistoryProps {
  messages: OrderMessage[];
  isLoading?: boolean;
}

export function MessageHistory({ messages, isLoading }: MessageHistoryProps) {
  const formatTimestamp = (timestamp: string) => {
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true,
    });
  };

  if (isLoading) {
    return (
      <div className="text-center py-4 text-gray-500">
        Loading messages...
      </div>
    );
  }

  if (messages.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        <MessageSquare className="w-8 h-8 mx-auto mb-2 opacity-50" />
        <p>No messages sent yet</p>
        <p className="text-sm">Messages you send will appear here</p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {messages.map((msg) => (
        <div
          key={msg.id}
          className="bg-gray-50 rounded-lg p-4 border border-gray-200"
        >
          <p className="text-gray-800">{msg.message}</p>
          <p className="text-xs text-gray-500 mt-2">
            Sent {formatTimestamp(msg.sentAt)}
          </p>
        </div>
      ))}
    </div>
  );
}

import { cn } from '@/lib/utils';
import type { OrderState } from '@/types';

export type FilterTab = 'new' | 'accepted' | 'preparing' | 'ready' | 'completed';

interface OrderFiltersProps {
  activeTab: FilterTab;
  onTabChange: (tab: FilterTab) => void;
  counts: {
    new: number;
    accepted: number;
    preparing: number;
    ready: number;
    completed: number;
  };
}

export function OrderFilters({ activeTab, onTabChange, counts }: OrderFiltersProps) {
  const tabs: { id: FilterTab; label: string; count: number }[] = [
    { id: 'new', label: 'New', count: counts.new },
    { id: 'accepted', label: 'Accepted', count: counts.accepted },
    { id: 'preparing', label: 'Preparing', count: counts.preparing },
    { id: 'ready', label: 'Ready', count: counts.ready },
    { id: 'completed', label: 'Completed', count: counts.completed },
  ];

  return (
    <div className="flex gap-2 mb-6 flex-wrap">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          onClick={() => onTabChange(tab.id)}
          className={cn(
            'px-4 py-2 rounded-lg font-medium transition',
            activeTab === tab.id
              ? 'bg-primary text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          )}
        >
          {tab.label} ({tab.count})
        </button>
      ))}
    </div>
  );
}

export function getStatesForTab(tab: FilterTab): OrderState[] {
  switch (tab) {
    case 'new':
      return ['APPROVED'];
    case 'accepted':
      return ['ACCEPTED'];
    case 'preparing':
      return ['PREPARING'];
    case 'ready':
      return ['READY_FOR_PICKUP'];
    case 'completed':
      return ['PICKED_UP', 'DELIVERED', 'CANCELLED'];
    default:
      return [];
  }
}

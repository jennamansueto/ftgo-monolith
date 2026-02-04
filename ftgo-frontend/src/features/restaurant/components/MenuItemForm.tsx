import { useState } from 'react';
import { Plus, X } from 'lucide-react';
import { cn } from '@/lib/utils';
import type { AddMenuItemRequest } from '@/types';

interface MenuItemFormProps {
  onSubmit: (item: AddMenuItemRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export function MenuItemForm({
  onSubmit,
  onCancel,
  isLoading,
}: MenuItemFormProps) {
  const [id, setId] = useState('');
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (id.trim() && name.trim() && price) {
      onSubmit({ id: id.trim(), name: name.trim(), price });
    }
  };

  const isValid = id.trim() && name.trim() && price && parseFloat(price) > 0;

  return (
    <form
      onSubmit={handleSubmit}
      className={cn(
        'bg-white rounded-xl p-6 shadow-sm border-2 border-primary',
        isLoading && 'opacity-50 pointer-events-none'
      )}
    >
      <h3 className="text-lg font-bold text-secondary mb-4">Add New Menu Item</h3>
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Item ID</label>
          <input
            type="text"
            value={id}
            onChange={(e) => setId(e.target.value)}
            className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
            placeholder="e.g., burger-01"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
            placeholder="e.g., Classic Burger"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Price ($)</label>
          <input
            type="number"
            step="0.01"
            min="0.01"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
            placeholder="0.00"
            required
          />
        </div>
        <div className="flex gap-2 pt-2">
          <button
            type="submit"
            disabled={isLoading || !isValid}
            className="flex-1 bg-primary text-white py-2 px-4 rounded-lg font-medium hover:bg-orange-600 transition flex items-center justify-center gap-2 disabled:opacity-50"
          >
            <Plus className="w-4 h-4" />
            Add Item
          </button>
          <button
            type="button"
            onClick={onCancel}
            disabled={isLoading}
            className="flex-1 bg-gray-200 text-gray-700 py-2 px-4 rounded-lg font-medium hover:bg-gray-300 transition flex items-center justify-center gap-2"
          >
            <X className="w-4 h-4" />
            Cancel
          </button>
        </div>
      </div>
    </form>
  );
}

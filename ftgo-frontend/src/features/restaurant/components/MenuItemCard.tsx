import { useState } from 'react';
import { Edit2, Trash2, Check, X } from 'lucide-react';
import { cn } from '@/lib/utils';
import { MoneyDisplay } from '@/components/common';
import type { MenuItem, UpdateMenuItemRequest } from '@/types';

interface MenuItemCardProps {
  item: MenuItem;
  onUpdate?: (itemId: string, updates: UpdateMenuItemRequest) => void;
  onDelete?: (itemId: string) => void;
  isLoading?: boolean;
}

export function MenuItemCard({
  item,
  onUpdate,
  onDelete,
  isLoading,
}: MenuItemCardProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState(item.name);
  const [editPrice, setEditPrice] = useState(item.price);

  const handleSave = () => {
    if (onUpdate && (editName !== item.name || editPrice !== item.price)) {
      onUpdate(item.id, { name: editName, price: editPrice });
    }
    setIsEditing(false);
  };

  const handleCancel = () => {
    setEditName(item.name);
    setEditPrice(item.price);
    setIsEditing(false);
  };

  const handleDelete = () => {
    if (onDelete && window.confirm(`Are you sure you want to delete "${item.name}"?`)) {
      onDelete(item.id);
    }
  };

  return (
    <div
      className={cn(
        'bg-white rounded-xl p-6 shadow-sm border-2 border-gray-200 transition-all',
        isLoading && 'opacity-50 pointer-events-none'
      )}
    >
      {isEditing ? (
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input
              type="text"
              value={editName}
              onChange={(e) => setEditName(e.target.value)}
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
              placeholder="Item name"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Price</label>
            <input
              type="number"
              step="0.01"
              min="0"
              value={editPrice}
              onChange={(e) => setEditPrice(e.target.value)}
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
              placeholder="0.00"
            />
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleSave}
              disabled={isLoading || !editName.trim() || !editPrice}
              className="flex-1 bg-success text-white py-2 px-4 rounded-lg font-medium hover:bg-green-600 transition flex items-center justify-center gap-2 disabled:opacity-50"
            >
              <Check className="w-4 h-4" />
              Save
            </button>
            <button
              onClick={handleCancel}
              disabled={isLoading}
              className="flex-1 bg-gray-200 text-gray-700 py-2 px-4 rounded-lg font-medium hover:bg-gray-300 transition flex items-center justify-center gap-2"
            >
              <X className="w-4 h-4" />
              Cancel
            </button>
          </div>
        </div>
      ) : (
        <>
          <div className="flex justify-between items-start mb-4">
            <div>
              <h3 className="text-lg font-bold text-secondary">{item.name}</h3>
              <p className="text-sm text-gray-500">ID: {item.id}</p>
            </div>
            <MoneyDisplay amount={item.price} className="text-xl font-bold text-primary" />
          </div>
          <div className="flex gap-2">
            {onUpdate && (
              <button
                onClick={() => setIsEditing(true)}
                disabled={isLoading}
                className="flex-1 bg-blue-500 text-white py-2 px-4 rounded-lg font-medium hover:bg-blue-600 transition flex items-center justify-center gap-2"
              >
                <Edit2 className="w-4 h-4" />
                Edit
              </button>
            )}
            {onDelete && (
              <button
                onClick={handleDelete}
                disabled={isLoading}
                className="px-4 py-2 border border-danger text-danger rounded-lg font-medium hover:bg-danger/5 transition flex items-center justify-center gap-2"
              >
                <Trash2 className="w-4 h-4" />
                Delete
              </button>
            )}
          </div>
        </>
      )}
    </div>
  );
}

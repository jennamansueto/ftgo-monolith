import { useState } from 'react';
import { RefreshCw, Plus, ArrowLeft, Settings, LayoutDashboard, ClipboardList, BookOpen } from 'lucide-react';
import { Link } from 'react-router-dom';
import { LoadingSpinner } from '@/components/common';
import { MenuItemCard, MenuItemForm } from '../components';
import {
  useMenuItems,
  useAddMenuItem,
  useUpdateMenuItem,
  useDeleteMenuItem,
} from '../hooks/useMenuItems';
import type { AddMenuItemRequest, UpdateMenuItemRequest } from '@/types';

const RESTAURANT_ID = 1;

export function MenuManagement() {
  const [showAddForm, setShowAddForm] = useState(false);

  const { data: menuItems = [], isLoading, refetch, isRefetching } = useMenuItems(RESTAURANT_ID);
  const addMenuItem = useAddMenuItem(RESTAURANT_ID);
  const updateMenuItem = useUpdateMenuItem(RESTAURANT_ID);
  const deleteMenuItem = useDeleteMenuItem(RESTAURANT_ID);

  const handleAddItem = (item: AddMenuItemRequest) => {
    addMenuItem.mutate(item, {
      onSuccess: () => {
        setShowAddForm(false);
      },
    });
  };

  const handleUpdateItem = (itemId: string, updates: UpdateMenuItemRequest) => {
    updateMenuItem.mutate({ itemId, updates });
  };

  const handleDeleteItem = (itemId: string) => {
    deleteMenuItem.mutate(itemId);
  };

  const isAnyMutating =
    addMenuItem.isPending ||
    updateMenuItem.isPending ||
    deleteMenuItem.isPending;

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-secondary text-white flex flex-col">
        <div className="p-6 border-b border-white/10">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
              <span className="font-bold">F</span>
            </div>
            <div>
              <p className="font-semibold">FTGO Restaurant</p>
              <p className="text-xs text-white/60">Dashboard</p>
            </div>
          </div>
        </div>
        <nav className="flex-1 p-4">
          <Link
            to="/"
            className="flex items-center gap-3 px-4 py-3 text-white/60 hover:bg-white/5 rounded-lg mb-2"
          >
            <LayoutDashboard className="w-5 h-5" />
            <span>Dashboard</span>
          </Link>
          <Link
            to="/"
            className="flex items-center gap-3 px-4 py-3 text-white/60 hover:bg-white/5 rounded-lg mb-2"
          >
            <ClipboardList className="w-5 h-5" />
            <span>Orders</span>
          </Link>
          <Link
            to="/menu"
            className="flex items-center gap-3 px-4 py-3 bg-white/10 rounded-lg mb-2"
          >
            <BookOpen className="w-5 h-5" />
            <span>Menu</span>
          </Link>
          <a
            href="#"
            className="flex items-center gap-3 px-4 py-3 text-white/60 hover:bg-white/5 rounded-lg"
          >
            <Settings className="w-5 h-5" />
            <span>Settings</span>
          </a>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col">
        {/* Header */}
        <header className="bg-white px-8 py-4 border-b flex items-center justify-between">
          <div className="flex items-center gap-4">
            <Link
              to="/"
              className="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition"
            >
              <ArrowLeft className="w-5 h-5" />
            </Link>
            <h1 className="text-2xl font-bold text-secondary">Menu Management</h1>
          </div>
          <div className="flex items-center gap-4">
            <button
              onClick={() => refetch()}
              disabled={isRefetching}
              className="flex items-center gap-2 px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition"
            >
              <RefreshCw className={`w-4 h-4 ${isRefetching ? 'animate-spin' : ''}`} />
              Refresh
            </button>
            {!showAddForm && (
              <button
                onClick={() => setShowAddForm(true)}
                className="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg font-medium hover:bg-orange-600 transition"
              >
                <Plus className="w-4 h-4" />
                Add Item
              </button>
            )}
          </div>
        </header>

        {/* Menu Items */}
        <div className="flex-1 overflow-auto p-8">
          <div className="mb-6">
            <p className="text-gray-600">
              {menuItems.length} menu item{menuItems.length !== 1 ? 's' : ''}
            </p>
          </div>

          {isLoading ? (
            <div className="flex items-center justify-center py-12">
              <LoadingSpinner size="lg" />
            </div>
          ) : (
            <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
              {showAddForm && (
                <MenuItemForm
                  onSubmit={handleAddItem}
                  onCancel={() => setShowAddForm(false)}
                  isLoading={addMenuItem.isPending}
                />
              )}
              {menuItems.length === 0 && !showAddForm ? (
                <div className="col-span-full text-center py-12 text-gray-500">
                  <BookOpen className="w-12 h-12 mx-auto mb-4 text-gray-300" />
                  <p className="text-lg">No menu items yet</p>
                  <p className="text-sm mt-2">Click "Add Item" to create your first menu item</p>
                </div>
              ) : (
                menuItems.map((item) => (
                  <MenuItemCard
                    key={item.id}
                    item={item}
                    onUpdate={handleUpdateItem}
                    onDelete={handleDeleteItem}
                    isLoading={isAnyMutating}
                  />
                ))
              )}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

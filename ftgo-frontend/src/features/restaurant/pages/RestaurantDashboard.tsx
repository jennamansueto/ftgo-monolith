import { useState, useMemo } from 'react';
import { RefreshCw, Settings, LayoutDashboard, ClipboardList, BookOpen } from 'lucide-react';
import { LoadingSpinner } from '@/components/common';
import { OrderCard, StatsCard, OrderFilters, getStatesForTab, type FilterTab } from '../components';
import {
  useOrders,
  useAcceptOrder,
  useMarkPreparing,
  useMarkReady,
  useCancelOrder,
  useSendMessage,
  filterOrdersByState,
  getOrderCounts,
} from '../hooks/useOrders';

export function RestaurantDashboard() {
  const [activeTab, setActiveTab] = useState<FilterTab>('new');
  
  const { data: orders = [], isLoading, refetch, isRefetching } = useOrders();
  const acceptOrder = useAcceptOrder();
  const markPreparing = useMarkPreparing();
  const markReady = useMarkReady();
  const cancelOrder = useCancelOrder();
  const sendMessage = useSendMessage();

  const counts = useMemo(() => {
    const orderCounts = getOrderCounts(orders);
    return {
      new: orderCounts.new,
      accepted: orderCounts.accepted,
      preparing: orderCounts.preparing,
      ready: orderCounts.ready,
      completed: orderCounts.pickedUp + orderCounts.delivered + orderCounts.cancelled,
    };
  }, [orders]);

  const filteredOrders = useMemo(() => {
    const states = getStatesForTab(activeTab);
    return filterOrdersByState(orders, states);
  }, [orders, activeTab]);

  const handleAccept = (orderId: number, readyBy: string) => {
    acceptOrder.mutate({ orderId, request: { readyBy } });
  };

  const handlePreparing = (orderId: number) => {
    markPreparing.mutate(orderId);
  };

  const handleReady = (orderId: number) => {
    markReady.mutate(orderId);
  };

  const handleCancel = (orderId: number) => {
    if (window.confirm('Are you sure you want to cancel this order?')) {
      cancelOrder.mutate(orderId);
    }
  };

  const handleSendMessage = (orderId: number, message: string) => {
    sendMessage.mutate({ orderId, request: { message } });
  };

  const isAnyMutating =
    acceptOrder.isPending ||
    markPreparing.isPending ||
    markReady.isPending ||
    cancelOrder.isPending;

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
          <a
            href="#"
            className="flex items-center gap-3 px-4 py-3 bg-white/10 rounded-lg mb-2"
          >
            <LayoutDashboard className="w-5 h-5" />
            <span>Dashboard</span>
          </a>
          <a
            href="#"
            className="flex items-center gap-3 px-4 py-3 text-white/60 hover:bg-white/5 rounded-lg mb-2"
          >
            <ClipboardList className="w-5 h-5" />
            <span>Orders</span>
          </a>
          <a
            href="#"
            className="flex items-center gap-3 px-4 py-3 text-white/60 hover:bg-white/5 rounded-lg mb-2"
          >
            <BookOpen className="w-5 h-5" />
            <span>Menu</span>
          </a>
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
          <h1 className="text-2xl font-bold text-secondary">Order Dashboard</h1>
          <div className="flex items-center gap-4">
            <button
              onClick={() => refetch()}
              disabled={isRefetching}
              className="flex items-center gap-2 px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition"
            >
              <RefreshCw className={`w-4 h-4 ${isRefetching ? 'animate-spin' : ''}`} />
              Refresh
            </button>
            <span className="text-sm text-gray-500">
              {new Date().toLocaleDateString('en-US', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric',
              })}
            </span>
          </div>
        </header>

        {/* Stats */}
        <div className="px-8 py-6 bg-gray-50 border-b">
          <div className="flex gap-4">
            <StatsCard title="New Orders" value={counts.new} color="primary" />
            <StatsCard title="Accepted" value={counts.accepted} color="warning" />
            <StatsCard title="Preparing" value={counts.preparing} color="warning" />
            <StatsCard title="Ready" value={counts.ready} color="success" />
            <StatsCard title="Completed Today" value={counts.completed} color="gray" />
          </div>
        </div>

        {/* Orders List */}
        <div className="flex-1 overflow-auto p-8">
          <OrderFilters
            activeTab={activeTab}
            onTabChange={setActiveTab}
            counts={counts}
          />

          {isLoading ? (
            <div className="flex items-center justify-center py-12">
              <LoadingSpinner size="lg" />
            </div>
          ) : filteredOrders.length === 0 ? (
            <div className="text-center py-12 text-gray-500">
              <p className="text-lg">No orders in this category</p>
              <p className="text-sm mt-2">Orders will appear here when they come in</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
              {filteredOrders.map((order) => (
                <OrderCard
                  key={order.orderId}
                  order={order}
                  onAccept={handleAccept}
                  onPreparing={handlePreparing}
                  onReady={handleReady}
                  onCancel={handleCancel}
                  onSendMessage={handleSendMessage}
                  isLoading={isAnyMutating}
                  isMessageLoading={sendMessage.isPending}
                />
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

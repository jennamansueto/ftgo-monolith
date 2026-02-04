import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ordersApi, type AcceptOrderRequest } from '@/api/orders';
import type { Order, OrderState, SendMessageRequest } from '@/types';

export function useOrders() {
  return useQuery({
    queryKey: ['orders'],
    queryFn: () => ordersApi.getOrders(),
    refetchInterval: 5000,
  });
}

export function useOrder(orderId: number) {
  return useQuery({
    queryKey: ['orders', orderId],
    queryFn: () => ordersApi.getOrder(orderId),
    enabled: orderId > 0,
  });
}

export function useAcceptOrder() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ orderId, request }: { orderId: number; request: AcceptOrderRequest }) =>
      ordersApi.acceptOrder(orderId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
}

export function useMarkPreparing() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (orderId: number) => ordersApi.markPreparing(orderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
}

export function useMarkReady() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (orderId: number) => ordersApi.markReady(orderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
}

export function useCancelOrder() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (orderId: number) => ordersApi.cancelOrder(orderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
}

export function filterOrdersByState(orders: Order[], states: OrderState[]): Order[] {
  return orders.filter(order => states.includes(order.state));
}

export function getOrderCounts(orders: Order[]) {
  return {
    new: orders.filter(o => o.state === 'APPROVED').length,
    accepted: orders.filter(o => o.state === 'ACCEPTED').length,
    preparing: orders.filter(o => o.state === 'PREPARING').length,
    ready: orders.filter(o => o.state === 'READY_FOR_PICKUP').length,
    pickedUp: orders.filter(o => o.state === 'PICKED_UP').length,
    delivered: orders.filter(o => o.state === 'DELIVERED').length,
    cancelled: orders.filter(o => o.state === 'CANCELLED').length,
  };
}

export function useSendMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ orderId, request }: { orderId: number; request: SendMessageRequest }) =>
      ordersApi.sendMessage(orderId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
}

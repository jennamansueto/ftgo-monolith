import { apiClient } from './client';
import type { Order, OrderMessage } from '@/types';

export interface GetOrdersParams {
  consumerId?: number;
  restaurantId?: number;
}

export interface AcceptOrderRequest {
  readyBy: string;
}

export interface SendMessageRequest {
  message: string;
}

export const ordersApi = {
  getOrders: async (params?: GetOrdersParams): Promise<Order[]> => {
    const queryParams = new URLSearchParams();
    if (params?.consumerId) {
      queryParams.append('consumerId', params.consumerId.toString());
    }
    const url = queryParams.toString() ? `/orders?${queryParams}` : '/orders';
    const response = await apiClient.get<Order[]>(url);
    return response.data;
  },

  getOrder: async (orderId: number): Promise<Order> => {
    const response = await apiClient.get<Order>(`/orders/${orderId}`);
    return response.data;
  },

  acceptOrder: async (orderId: number, request: AcceptOrderRequest): Promise<void> => {
    await apiClient.post(`/orders/${orderId}/accept`, request);
  },

  markPreparing: async (orderId: number): Promise<void> => {
    await apiClient.post(`/orders/${orderId}/preparing`);
  },

  markReady: async (orderId: number): Promise<void> => {
    await apiClient.post(`/orders/${orderId}/ready`);
  },

  cancelOrder: async (orderId: number): Promise<void> => {
    await apiClient.post(`/orders/${orderId}/cancel`, {});
  },

  getMessages: async (orderId: number): Promise<OrderMessage[]> => {
    const response = await apiClient.get<OrderMessage[]>(`/orders/${orderId}/messages`);
    return response.data;
  },

  sendMessage: async (orderId: number, request: SendMessageRequest): Promise<OrderMessage> => {
    const response = await apiClient.post<OrderMessage>(`/orders/${orderId}/messages`, request);
    return response.data;
  },
};

import { apiClient } from './client';
import type { Order, OrderTracking } from '@/types';

export interface GetOrdersParams {
  consumerId?: number;
  restaurantId?: number;
}

export interface AcceptOrderRequest {
  readyBy: string;
}

export interface UpdateEtaRequest {
  estimatedPickupTime?: string;
  estimatedDeliveryTime?: string;
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

  getOrderTracking: async (orderId: number): Promise<OrderTracking> => {
    const response = await apiClient.get<OrderTracking>(`/orders/${orderId}/tracking`);
    return response.data;
  },

  updateEta: async (orderId: number, request: UpdateEtaRequest): Promise<void> => {
    await apiClient.put(`/orders/${orderId}/eta`, request);
  },
};

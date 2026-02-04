import { apiClient } from './client';
import type { Restaurant, MenuItem, AddMenuItemRequest, UpdateMenuItemRequest, GetMenuResponse } from '@/types';

export const restaurantsApi = {
  getRestaurant: async (restaurantId: number): Promise<Restaurant> => {
    const response = await apiClient.get<Restaurant>(`/restaurants/${restaurantId}`);
    return response.data;
  },

  getRestaurants: async (): Promise<Restaurant[]> => {
    const response = await apiClient.get<Restaurant[]>('/restaurants');
    return response.data;
  },

  getMenu: async (restaurantId: number): Promise<MenuItem[]> => {
    const response = await apiClient.get<GetMenuResponse>(`/restaurants/${restaurantId}/menu`);
    return response.data.menuItems;
  },

  addMenuItem: async (restaurantId: number, item: AddMenuItemRequest): Promise<MenuItem> => {
    const response = await apiClient.post<MenuItem>(`/restaurants/${restaurantId}/menu`, item);
    return response.data;
  },

  updateMenuItem: async (restaurantId: number, itemId: string, updates: UpdateMenuItemRequest): Promise<MenuItem> => {
    const response = await apiClient.put<MenuItem>(`/restaurants/${restaurantId}/menu/${itemId}`, updates);
    return response.data;
  },

  deleteMenuItem: async (restaurantId: number, itemId: string): Promise<void> => {
    await apiClient.delete(`/restaurants/${restaurantId}/menu/${itemId}`);
  },
};

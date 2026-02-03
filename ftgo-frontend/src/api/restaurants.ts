import { apiClient } from './client';
import type { Restaurant } from '@/types';

export const restaurantsApi = {
  getRestaurant: async (restaurantId: number): Promise<Restaurant> => {
    const response = await apiClient.get<Restaurant>(`/restaurants/${restaurantId}`);
    return response.data;
  },

  getRestaurants: async (): Promise<Restaurant[]> => {
    const response = await apiClient.get<Restaurant[]>('/restaurants');
    return response.data;
  },
};

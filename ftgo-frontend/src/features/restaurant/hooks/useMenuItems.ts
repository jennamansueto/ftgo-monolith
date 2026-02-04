import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { restaurantsApi } from '@/api/restaurants';
import type { AddMenuItemRequest, UpdateMenuItemRequest } from '@/types';

export function useMenuItems(restaurantId: number) {
  return useQuery({
    queryKey: ['menuItems', restaurantId],
    queryFn: () => restaurantsApi.getMenu(restaurantId),
    enabled: restaurantId > 0,
  });
}

export function useAddMenuItem(restaurantId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (item: AddMenuItemRequest) =>
      restaurantsApi.addMenuItem(restaurantId, item),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['menuItems', restaurantId] });
    },
  });
}

export function useUpdateMenuItem(restaurantId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ itemId, updates }: { itemId: string; updates: UpdateMenuItemRequest }) =>
      restaurantsApi.updateMenuItem(restaurantId, itemId, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['menuItems', restaurantId] });
    },
  });
}

export function useDeleteMenuItem(restaurantId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (itemId: string) =>
      restaurantsApi.deleteMenuItem(restaurantId, itemId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['menuItems', restaurantId] });
    },
  });
}

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ordersApi } from '@/api/orders';

export function useOrderMessages(orderId: number) {
  return useQuery({
    queryKey: ['orders', orderId, 'messages'],
    queryFn: () => ordersApi.getMessages(orderId),
    enabled: orderId > 0,
  });
}

export function useSendMessage() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ orderId, message }: { orderId: number; message: string }) =>
      ordersApi.sendMessage(orderId, { message }),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['orders', variables.orderId, 'messages'] });
    },
  });
}

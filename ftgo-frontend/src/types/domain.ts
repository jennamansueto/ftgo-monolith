// Money is serialized as a string by the backend's MoneyModule
export type Money = string;

// Helper functions for Money handling
export const moneyUtils = {
  format: (amount: Money): string => {
    const num = parseFloat(amount);
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(num);
  },
  
  parse: (display: string): Money => {
    return display.replace(/[^0-9.]/g, '');
  },
  
  add: (a: Money, b: Money): Money => {
    return (parseFloat(a) + parseFloat(b)).toFixed(2);
  },
  
  multiply: (amount: Money, quantity: number): Money => {
    return (parseFloat(amount) * quantity).toFixed(2);
  },
};

export interface PersonName {
  firstName: string;
  lastName: string;
}

export interface Address {
  street1: string;
  street2?: string;
  city: string;
  state: string;
  zip: string;
}

export type OrderState = 
  | 'APPROVED'
  | 'ACCEPTED'
  | 'PREPARING'
  | 'READY_FOR_PICKUP'
  | 'PICKED_UP'
  | 'DELIVERED'
  | 'CANCELLED';

export interface OrderLineItem {
  menuItemId: string;
  name: string;
  price: Money;
  quantity: number;
}

export interface Order {
  orderId: number;
  state: OrderState;
  orderTotal: Money;
  restaurantId?: number;
  restaurantName?: string;
  consumerId?: number;
  lineItems?: OrderLineItem[];
  assignedCourier?: number | null;
  courierActions?: CourierAction[] | null;
}

export interface CourierAction {
  type: 'PICKUP' | 'DROPOFF';
  address: Address;
  time: string;
}

export interface MenuItem {
  id: string;
  name: string;
  price: Money;
}

export interface Restaurant {
  id: number;
  name: string;
  menuItems?: MenuItem[];
}

export interface Consumer {
  id: number;
  name: PersonName;
}

export interface OrderMessage {
  id: number;
  orderId: number;
  message: string;
  sentAt: string;
}

export interface SendMessageRequest {
  message: string;
}

package net.chrisrichardson.ftgo.restaurantservice.domain;

public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException(Long orderId) {
    super("Order not found: " + orderId);
  }
}

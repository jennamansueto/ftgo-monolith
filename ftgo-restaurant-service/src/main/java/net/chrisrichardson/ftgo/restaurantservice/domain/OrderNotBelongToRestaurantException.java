package net.chrisrichardson.ftgo.restaurantservice.domain;

public class OrderNotBelongToRestaurantException extends RuntimeException {
  public OrderNotBelongToRestaurantException(Long orderId, Long restaurantId) {
    super("Order " + orderId + " does not belong to restaurant " + restaurantId);
  }
}

package net.chrisrichardson.ftgo.orderservice.restaurantclient;

public class RestaurantNotFoundException extends RuntimeException {

  private final long restaurantId;

  public RestaurantNotFoundException(long restaurantId) {
    super("Restaurant not found: " + restaurantId);
    this.restaurantId = restaurantId;
  }

  public long getRestaurantId() {
    return restaurantId;
  }
}

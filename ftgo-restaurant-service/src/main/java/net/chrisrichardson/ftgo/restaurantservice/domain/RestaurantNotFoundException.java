package net.chrisrichardson.ftgo.restaurantservice.domain;

public class RestaurantNotFoundException extends RuntimeException {
  public RestaurantNotFoundException(Long restaurantId) {
    super("Restaurant not found: " + restaurantId);
  }
}

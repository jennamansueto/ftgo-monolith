package net.chrisrichardson.ftgo.restaurantservice.domain;

public class RestaurantNotFoundException extends RuntimeException {

  public RestaurantNotFoundException(long restaurantId) {
    super("Restaurant not found: " + restaurantId);
  }
}

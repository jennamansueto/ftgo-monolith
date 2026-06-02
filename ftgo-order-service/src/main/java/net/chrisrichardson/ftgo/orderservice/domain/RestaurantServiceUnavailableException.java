package net.chrisrichardson.ftgo.orderservice.domain;

public class RestaurantServiceUnavailableException extends RuntimeException {
  public RestaurantServiceUnavailableException(long restaurantId, Throwable cause) {
    super("Restaurant service is unavailable while fetching restaurant " + restaurantId, cause);
  }
}

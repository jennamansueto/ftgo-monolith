package net.chrisrichardson.ftgo.orderservice.restaurantclient;

public class RestaurantServiceUnavailableException extends RuntimeException {

  public RestaurantServiceUnavailableException(String message) {
    super(message);
  }

  public RestaurantServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

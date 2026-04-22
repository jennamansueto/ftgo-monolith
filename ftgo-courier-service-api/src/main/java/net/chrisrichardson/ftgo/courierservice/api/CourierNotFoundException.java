package net.chrisrichardson.ftgo.courierservice.api;

public class CourierNotFoundException extends RuntimeException {

  public CourierNotFoundException(long courierId) {
    super("Courier not found: " + courierId);
  }

  public CourierNotFoundException(String message) {
    super(message);
  }
}

package net.chrisrichardson.ftgo.orderservice.courier;

public class CourierServiceUnavailableException extends RuntimeException {

  public CourierServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

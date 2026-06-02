package net.chrisrichardson.ftgo.orderservice.domain;

public class CourierServiceUnavailableException extends RuntimeException {

  public CourierServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

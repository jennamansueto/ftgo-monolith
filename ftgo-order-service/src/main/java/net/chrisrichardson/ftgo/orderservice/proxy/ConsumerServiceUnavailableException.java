package net.chrisrichardson.ftgo.orderservice.proxy;

public class ConsumerServiceUnavailableException extends RuntimeException {

  public ConsumerServiceUnavailableException(String message) {
    super(message);
  }

  public ConsumerServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

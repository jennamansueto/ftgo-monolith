package net.chrisrichardson.ftgo.orderservice.client;

public class ConsumerServiceUnavailableException extends RuntimeException {

  public ConsumerServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

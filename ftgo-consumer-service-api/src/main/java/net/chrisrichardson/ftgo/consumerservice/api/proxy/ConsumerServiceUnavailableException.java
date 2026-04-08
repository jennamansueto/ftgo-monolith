package net.chrisrichardson.ftgo.consumerservice.api.proxy;

public class ConsumerServiceUnavailableException extends RuntimeException {

  public ConsumerServiceUnavailableException() {
  }

  public ConsumerServiceUnavailableException(String message) {
    super(message);
  }

  public ConsumerServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

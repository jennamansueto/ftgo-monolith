package net.chrisrichardson.ftgo.consumerservice.client;

public class ConsumerServiceUnavailableException extends RuntimeException {

  public ConsumerServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

package net.chrisrichardson.ftgo.orderservice.domain;

public class ConsumerServiceUnavailableException extends RuntimeException {

  public ConsumerServiceUnavailableException(Throwable cause) {
    super("Consumer service is unavailable", cause);
  }
}

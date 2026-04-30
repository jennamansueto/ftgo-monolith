package net.chrisrichardson.ftgo.orderservice.domain;

public class ConsumerServiceUnavailableException extends RuntimeException {

  public ConsumerServiceUnavailableException(long consumerId, Throwable cause) {
    super("Consumer service unavailable while validating consumerId: " + consumerId, cause);
  }
}

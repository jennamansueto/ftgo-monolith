package net.chrisrichardson.ftgo.orderservice.domain;

public class ConsumerValidationFailedException extends RuntimeException {

  public ConsumerValidationFailedException(long consumerId, Throwable cause) {
    super("Consumer validation failed for consumer " + consumerId, cause);
  }
}

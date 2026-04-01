package net.chrisrichardson.ftgo.orderservice.domain;

public class ConsumerValidationFailedException extends RuntimeException {

  public ConsumerValidationFailedException(long consumerId) {
    super("Consumer validation failed for consumerId=" + consumerId);
  }
}

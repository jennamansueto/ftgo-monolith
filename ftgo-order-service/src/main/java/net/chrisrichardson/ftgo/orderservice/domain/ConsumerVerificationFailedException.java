package net.chrisrichardson.ftgo.orderservice.domain;

public class ConsumerVerificationFailedException extends RuntimeException {
  public ConsumerVerificationFailedException(long consumerId) {
    super("Consumer verification failed for consumer: " + consumerId);
  }
}

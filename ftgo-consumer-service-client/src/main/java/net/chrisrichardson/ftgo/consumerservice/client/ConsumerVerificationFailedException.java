package net.chrisrichardson.ftgo.consumerservice.client;

public class ConsumerVerificationFailedException extends RuntimeException {
  public ConsumerVerificationFailedException(long consumerId) {
    super("Consumer verification failed: " + consumerId);
  }
}

package net.chrisrichardson.ftgo.orderservice.domain;

public class ConsumerNotFoundException extends RuntimeException {
  public ConsumerNotFoundException(long consumerId) {
    super("Consumer not found: " + consumerId);
  }
}

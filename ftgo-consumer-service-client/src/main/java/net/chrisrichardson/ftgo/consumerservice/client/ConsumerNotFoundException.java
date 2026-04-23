package net.chrisrichardson.ftgo.consumerservice.client;

public class ConsumerNotFoundException extends RuntimeException {
  public ConsumerNotFoundException(long consumerId) {
    super("Consumer not found: " + consumerId);
  }
}

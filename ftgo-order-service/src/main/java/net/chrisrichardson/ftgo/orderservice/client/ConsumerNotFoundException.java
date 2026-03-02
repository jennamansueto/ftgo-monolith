package net.chrisrichardson.ftgo.orderservice.client;

public class ConsumerNotFoundException extends RuntimeException {

  public ConsumerNotFoundException(long consumerId) {
    super("Consumer not found: " + consumerId);
  }
}

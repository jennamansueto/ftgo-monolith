package net.chrisrichardson.ftgo.orderservice.client;

public class ConsumerNotFoundException extends RuntimeException {
  private final long consumerId;

  public ConsumerNotFoundException(long consumerId) {
    super("Consumer not found: " + consumerId);
    this.consumerId = consumerId;
  }

  public long getConsumerId() {
    return consumerId;
  }
}

package net.chrisrichardson.ftgo.orderservice.domain;

public class ConsumerNotFoundException extends ConsumerVerificationFailedException {

  private long consumerId;

  public ConsumerNotFoundException(long consumerId) {
    this.consumerId = consumerId;
  }

  public long getConsumerId() {
    return consumerId;
  }
}

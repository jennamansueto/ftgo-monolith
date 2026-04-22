package net.chrisrichardson.ftgo.consumerservice.client;

public class ConsumerNotFoundException extends ConsumerVerificationFailedException {

  public ConsumerNotFoundException(long consumerId) {
    super("Consumer not found: " + consumerId);
  }
}

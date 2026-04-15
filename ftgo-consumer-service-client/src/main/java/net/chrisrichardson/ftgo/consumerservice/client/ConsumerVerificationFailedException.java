package net.chrisrichardson.ftgo.consumerservice.client;

public class ConsumerVerificationFailedException extends RuntimeException {

  public ConsumerVerificationFailedException(String message) {
    super(message);
  }
}

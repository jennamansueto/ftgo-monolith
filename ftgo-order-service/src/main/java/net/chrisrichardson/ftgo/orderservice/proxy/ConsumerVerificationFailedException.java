package net.chrisrichardson.ftgo.orderservice.proxy;

public class ConsumerVerificationFailedException extends RuntimeException {

  public ConsumerVerificationFailedException(String message) {
    super(message);
  }
}

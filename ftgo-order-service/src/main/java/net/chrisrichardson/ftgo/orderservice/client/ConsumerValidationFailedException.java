package net.chrisrichardson.ftgo.orderservice.client;

public class ConsumerValidationFailedException extends RuntimeException {

  public ConsumerValidationFailedException(String message) {
    super(message);
  }
}

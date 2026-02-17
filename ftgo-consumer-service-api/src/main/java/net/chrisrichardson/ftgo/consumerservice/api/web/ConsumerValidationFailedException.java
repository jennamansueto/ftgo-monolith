package net.chrisrichardson.ftgo.consumerservice.api.web;

public class ConsumerValidationFailedException extends RuntimeException {

  public ConsumerValidationFailedException() {
  }

  public ConsumerValidationFailedException(String message) {
    super(message);
  }

  public ConsumerValidationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}

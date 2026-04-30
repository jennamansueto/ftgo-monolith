package net.chrisrichardson.ftgo.consumerservice.api;

public class ConsumerVerificationException extends RuntimeException {

  public ConsumerVerificationException(String message) {
    super(message);
  }

  public ConsumerVerificationException(String message, Throwable cause) {
    super(message, cause);
  }
}

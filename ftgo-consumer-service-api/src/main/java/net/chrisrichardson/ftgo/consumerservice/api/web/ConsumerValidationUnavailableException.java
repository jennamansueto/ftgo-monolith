package net.chrisrichardson.ftgo.consumerservice.api.web;

public class ConsumerValidationUnavailableException extends RuntimeException {

  public ConsumerValidationUnavailableException() {
  }

  public ConsumerValidationUnavailableException(String message) {
    super(message);
  }

  public ConsumerValidationUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}

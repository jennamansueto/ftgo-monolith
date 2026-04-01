package net.chrisrichardson.ftgo.orderservice.domain.client;

public class ConsumerValidationException extends RuntimeException {

  public ConsumerValidationException(String message) {
    super(message);
  }
}

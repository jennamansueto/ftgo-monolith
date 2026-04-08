package net.chrisrichardson.ftgo.consumerservice.api.proxy;

public class ConsumerVerificationException extends RuntimeException {

  public ConsumerVerificationException() {
  }

  public ConsumerVerificationException(String message) {
    super(message);
  }
}

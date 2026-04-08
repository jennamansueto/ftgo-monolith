package net.chrisrichardson.ftgo.consumerservice.api.proxy;

public class ConsumerNotFoundException extends RuntimeException {

  public ConsumerNotFoundException() {
  }

  public ConsumerNotFoundException(String message) {
    super(message);
  }
}

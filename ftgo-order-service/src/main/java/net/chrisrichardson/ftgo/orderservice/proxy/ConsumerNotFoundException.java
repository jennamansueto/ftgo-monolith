package net.chrisrichardson.ftgo.orderservice.proxy;

public class ConsumerNotFoundException extends RuntimeException {

  public ConsumerNotFoundException(String message) {
    super(message);
  }
}

package net.chrisrichardson.ftgo.consumerservice.client;

public class ConsumerNotFoundException extends RuntimeException {

  public ConsumerNotFoundException(String message) {
    super(message);
  }
}

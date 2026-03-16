package net.chrisrichardson.ftgo.orderservice.client;

public class NoCourierAvailableException extends RuntimeException {

  public NoCourierAvailableException(long orderId) {
    super("No courier available for order " + orderId);
  }
}

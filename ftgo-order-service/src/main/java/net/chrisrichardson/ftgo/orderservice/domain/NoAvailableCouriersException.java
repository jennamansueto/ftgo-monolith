package net.chrisrichardson.ftgo.orderservice.domain;

public class NoAvailableCouriersException extends RuntimeException {
  public NoAvailableCouriersException() {
    super("No couriers are available");
  }
}

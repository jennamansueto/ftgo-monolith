package net.chrisrichardson.ftgo.orderservice.domain;

public class NoAvailableCourierException extends RuntimeException {

  public NoAvailableCourierException() {
    super("No courier is currently available to schedule the delivery");
  }
}

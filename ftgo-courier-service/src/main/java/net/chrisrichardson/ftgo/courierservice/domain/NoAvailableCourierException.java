package net.chrisrichardson.ftgo.courierservice.domain;

public class NoAvailableCourierException extends RuntimeException {

  public NoAvailableCourierException() {
    super("No courier is currently available");
  }
}

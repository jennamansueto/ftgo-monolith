package net.chrisrichardson.ftgo.courierservice.api;

public class AvailableCourierDTO {

  private long id;

  public AvailableCourierDTO() {
  }

  public AvailableCourierDTO(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}

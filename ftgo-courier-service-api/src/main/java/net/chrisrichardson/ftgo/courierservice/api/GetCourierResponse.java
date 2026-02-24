package net.chrisrichardson.ftgo.courierservice.api;

public class GetCourierResponse {
  private long id;
  private boolean available;

  public GetCourierResponse() {
  }

  public GetCourierResponse(long id, boolean available) {
    this.id = id;
    this.available = available;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}

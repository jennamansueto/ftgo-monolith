package net.chrisrichardson.ftgo.courierservice.web;

import net.chrisrichardson.ftgo.courierservice.persistence.CourierAction;

import java.util.List;

public class GetCourierResponse {

  private long id;
  private boolean available;
  private List<CourierAction> actions;

  private GetCourierResponse() {
  }

  public GetCourierResponse(long id, boolean available, List<CourierAction> actions) {
    this.id = id;
    this.available = available;
    this.actions = actions;
  }

  public long getId() {
    return id;
  }

  public boolean isAvailable() {
    return available;
  }

  public List<CourierAction> getActions() {
    return actions;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public void setActions(List<CourierAction> actions) {
    this.actions = actions;
  }
}

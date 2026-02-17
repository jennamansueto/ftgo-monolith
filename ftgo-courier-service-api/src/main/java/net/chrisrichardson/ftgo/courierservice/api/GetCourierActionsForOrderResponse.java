package net.chrisrichardson.ftgo.courierservice.api;

import java.util.List;

public class GetCourierActionsForOrderResponse {
  private List<CourierActionDto> actions;

  public GetCourierActionsForOrderResponse() {
  }

  public GetCourierActionsForOrderResponse(List<CourierActionDto> actions) {
    this.actions = actions;
  }

  public List<CourierActionDto> getActions() {
    return actions;
  }

  public void setActions(List<CourierActionDto> actions) {
    this.actions = actions;
  }
}

package net.chrisrichardson.ftgo.courierservice.api;

import java.util.List;

public class AddActionsRequest {

  private List<ActionDTO> actions;

  private AddActionsRequest() {
  }

  public AddActionsRequest(List<ActionDTO> actions) {
    this.actions = actions;
  }

  public List<ActionDTO> getActions() {
    return actions;
  }

  public void setActions(List<ActionDTO> actions) {
    this.actions = actions;
  }
}

package net.chrisrichardson.ftgo.courierservice.domain;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Plan {

  @ElementCollection
  @CollectionTable(name = "courier_actions", joinColumns = @JoinColumn(name = "courier_id"))
  private List<CourierAction> actions = new LinkedList<>();

  public void add(CourierAction action) {
    actions.add(action);
  }

  public void removeDelivery(long orderId) {
    actions = actions.stream().filter(action -> !action.actionFor(orderId)).collect(Collectors.toList());
  }

  public List<CourierAction> getActions() {
    return actions;
  }

  public List<CourierAction> actionsForDelivery(long orderId) {
    return actions.stream().filter(action -> action.actionFor(orderId)).collect(Collectors.toList());
  }
}

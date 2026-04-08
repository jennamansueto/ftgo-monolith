package net.chrisrichardson.ftgo.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Embeddable
public class Action {

  @Enumerated(EnumType.STRING)
  private ActionType type;
  private LocalDateTime time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", insertable = false, updatable = false)
  private Order order;

  @Column(name = "order_id")
  private Long orderId;

  private Action() {
  }

  public Action(ActionType type, Order order, LocalDateTime time) {
    this.type = type;
    this.order = order;
    this.orderId = order != null ? order.getId() : null;
    this.time = time;
  }

  public Action(ActionType type, long orderId, LocalDateTime time) {
    this.type = type;
    this.orderId = orderId;
    this.time = time;
  }

  public boolean actionFor(Order order) {
    if (this.order != null) {
      return this.order.getId().equals(order.getId());
    }
    return this.orderId != null && this.orderId.equals(order.getId());
  }

  public static Action makePickup(Order order) {
    return new Action(ActionType.PICKUP, order, null);
  }

  public static Action makeDropoff(Order order, LocalDateTime deliveryTime) {
    return new Action(ActionType.DROPOFF, order, deliveryTime);
  }

  public static Action makePickupForOrder(long orderId, LocalDateTime time) {
    return new Action(ActionType.PICKUP, orderId, time);
  }

  public static Action makeDropoffForOrder(long orderId, LocalDateTime time) {
    return new Action(ActionType.DROPOFF, orderId, time);
  }


  public ActionType getType() {
    return type;
  }

}

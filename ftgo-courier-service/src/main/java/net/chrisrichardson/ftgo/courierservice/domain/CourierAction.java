package net.chrisrichardson.ftgo.courierservice.domain;

import net.chrisrichardson.ftgo.courierservice.api.CourierActionType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Embeddable
public class CourierAction {

  @Enumerated(EnumType.STRING)
  private CourierActionType type;

  private LocalDateTime time;

  @Column(name = "order_id")
  private Long orderId;

  CourierAction() {
  }

  public CourierAction(CourierActionType type, long orderId, LocalDateTime time) {
    this.type = type;
    this.orderId = orderId;
    this.time = time;
  }

  public boolean actionFor(long orderId) {
    return this.orderId != null && this.orderId == orderId;
  }

  public static CourierAction makePickup(long orderId) {
    return new CourierAction(CourierActionType.PICKUP, orderId, null);
  }

  public static CourierAction makeDropoff(long orderId, LocalDateTime deliveryTime) {
    return new CourierAction(CourierActionType.DROPOFF, orderId, deliveryTime);
  }

  public CourierActionType getType() {
    return type;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public Long getOrderId() {
    return orderId;
  }
}

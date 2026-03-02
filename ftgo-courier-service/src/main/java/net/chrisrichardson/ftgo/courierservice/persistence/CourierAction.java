package net.chrisrichardson.ftgo.courierservice.persistence;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Embeddable
public class CourierAction {

  @Enumerated(EnumType.STRING)
  private CourierActionType type;
  private LocalDateTime time;
  private Long orderId;

  private CourierAction() {
  }

  public CourierAction(CourierActionType type, Long orderId, LocalDateTime time) {
    this.type = type;
    this.orderId = orderId;
    this.time = time;
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

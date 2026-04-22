package net.chrisrichardson.ftgo.courierservice.api;

import java.time.LocalDateTime;

public class CourierActionDTO {

  private CourierActionType type;
  private LocalDateTime time;
  private Long orderId;

  public CourierActionDTO() {
  }

  public CourierActionDTO(CourierActionType type, LocalDateTime time, Long orderId) {
    this.type = type;
    this.time = time;
    this.orderId = orderId;
  }

  public CourierActionType getType() {
    return type;
  }

  public void setType(CourierActionType type) {
    this.type = type;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }
}

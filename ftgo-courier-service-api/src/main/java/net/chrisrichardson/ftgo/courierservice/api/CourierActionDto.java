package net.chrisrichardson.ftgo.courierservice.api;

import java.time.LocalDateTime;

public class CourierActionDto {
  private String type;
  private long orderId;
  private LocalDateTime time;

  public CourierActionDto() {
  }

  public CourierActionDto(String type, long orderId, LocalDateTime time) {
    this.type = type;
    this.orderId = orderId;
    this.time = time;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }
}
